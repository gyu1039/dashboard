package kig.dashboard.post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kig.dashboard.post.cond.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static kig.dashboard.member.entity.QMember.member;
import static kig.dashboard.post.QPost.post;
import static org.springframework.util.StringUtils.hasLength;

@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository{

    private final JPAQueryFactory query;

    public CustomPostRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable) {

        List<Post> content = query.selectFrom(post)
                .where(
                        contentHasStr(postSearchCondition.getContent()),
                        titleHasStr(postSearchCondition.getTitle())
                )
                .leftJoin(post.writer, member)
                .fetchJoin()
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageNumber())
                .fetch();

        JPAQuery<Post> countQuery = query.selectFrom(post)
                .where(
                        contentHasStr(postSearchCondition.getContent()),
                        titleHasStr(postSearchCondition.getTitle())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression contentHasStr(String content) {
        return hasLength(content) ? post.content.contains(content) : null;
    }

    private BooleanExpression titleHasStr(String title) {
        return hasLength(title) ? post.title.contains(title) : null;
    }
}
