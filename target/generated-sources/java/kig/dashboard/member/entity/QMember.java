package kig.dashboard.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -919147088L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final kig.dashboard.global.domain.QBaseTimeEntity _super = new kig.dashboard.global.domain.QBaseTimeEntity(this);

    public final ListPath<kig.dashboard.comment.Comment, kig.dashboard.comment.QComment> commentList = this.<kig.dashboard.comment.Comment, kig.dashboard.comment.QComment>createList("commentList", kig.dashboard.comment.Comment.class, kig.dashboard.comment.QComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final QGroup group;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<kig.dashboard.post.entity.Post, kig.dashboard.post.entity.QPost> postList = this.<kig.dashboard.post.entity.Post, kig.dashboard.post.entity.QPost>createList("postList", kig.dashboard.post.entity.Post.class, kig.dashboard.post.entity.QPost.class, PathInits.DIRECT2);

    public final StringPath refreshToken = createString("refreshToken");

    public final ListPath<RoleMember, QRoleMember> roleMembers = this.<RoleMember, QRoleMember>createList("roleMembers", RoleMember.class, QRoleMember.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.group = inits.isInitialized("group") ? new QGroup(forProperty("group")) : null;
    }

}

