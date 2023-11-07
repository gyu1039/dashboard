package kig.dashboard.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoleMember is a Querydsl query type for RoleMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoleMember extends EntityPathBase<RoleMember> {

    private static final long serialVersionUID = -249745722L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoleMember roleMember = new QRoleMember("roleMember");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QRole role;

    public QRoleMember(String variable) {
        this(RoleMember.class, forVariable(variable), INITS);
    }

    public QRoleMember(Path<? extends RoleMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoleMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoleMember(PathMetadata metadata, PathInits inits) {
        this(RoleMember.class, metadata, inits);
    }

    public QRoleMember(Class<? extends RoleMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
        this.role = inits.isInitialized("role") ? new QRole(forProperty("role")) : null;
    }

}

