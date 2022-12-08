//package com.example.myoceanproject.repository;
//
//import com.example.myoceanproject.domain.*;
//import com.example.myoceanproject.type.AskCategory;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//import static com.example.myoceanproject.entity.QAsk.ask;
//
//@Repository
//public class MyAskRepositoryImpl implements MyAskCustomRepository{
//
//    @Autowired
//    private JPAQueryFactory jpaQueryFactory;
//
//    @Override
//    public Page<AskDTO> findAll(Pageable pageable, Long userId) {
//        List<AskDTO> posts = jpaQueryFactory.select(new QAskDTO(
//                ask.user.userId,
//                ask.askStatus,
//                ask.askTitle,
//                ask.askContent,
//                ask.askCategory,
//                ask.createDate,
//                ask.updatedDate
//        ))
//        .from(ask)
//        .where(ask.user.userId.eq(userId))
//        .orderBy(ask.createDate.desc())
//        .offset(pageable.getOffset())
//        .limit(pageable.getPageSize()).fetch();
//
//        long total = jpaQueryFactory.selectFrom(ask)
//                .where(ask.user.userId.eq(userId))
//                .fetch().size();
//
//        return new PageImpl<>(posts, pageable, total);
//    }
//
//    @Override
//    public Page<AskDTO> findAll(Pageable pageable, Criteria criteria, Long userId) {
//        List<AskDTO> asks = jpaQueryFactory.select(new QAskDTO(
//                ask.user.userId,
//                ask.askStatus,
//                ask.askTitle,
//                ask.askContent,
//                ask.askCategory,
//                ask.createDate,
//                ask.updatedDate
//        ))
//                .from(ask)
//                .where(ask.user.userId.eq(userId))
//                .orderBy(ask.createDate.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize()).fetch();
//
//        long total = jpaQueryFactory.selectFrom(ask)
//                .where(ask.user.userId.eq(userId))
//                .fetch().size();
//
//        return new PageImpl<>(asks, pageable, total);
//    }
//
//    @Override
//    public Page<AskDTO> findAllByCategory(Pageable pageable, AskCategory askCategory, Long userId) {
//        List<AskDTO> posts = jpaQueryFactory.select(new QAskDTO(
//                ask.user.userId,
//                ask.askStatus,
//                ask.askTitle,
//                ask.askContent,
//                ask.askCategory,
//                ask.createDate,
//                ask.updatedDate
//        ))
//                .from(ask)
//                .where(ask.askCategory.eq(askCategory))
//                .orderBy(ask.createDate.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize()).fetch();
//
//        long total = jpaQueryFactory.selectFrom(ask)
//                .where(ask.user.userId.eq(userId))
//                .fetch().size();
//
//        return new PageImpl<>(posts, pageable, total);
//    }
//
//    @Override
//    public Page<AskDTO> findAllByCategory(Pageable pageable, AskCategory askCategory, Criteria criteria, Long userId) {
//        List<AskDTO> posts = jpaQueryFactory.select(new QAskDTO(
//                ask.user.userId,
//                ask.askStatus,
//                ask.askTitle,
//                ask.askContent,
//                ask.askCategory,
//                ask.createDate,
//                ask.updatedDate
//        ))
//                .from(ask)
//                .where(ask.askCategory.eq(askCategory))
//                .orderBy(ask.createDate.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize()).fetch();
//
//        long total = jpaQueryFactory.selectFrom(ask)
//                .where(ask.user.userId.eq(userId))
//                .fetch().size();
//
//        return new PageImpl<>(posts, pageable, total);
//    }
//}
