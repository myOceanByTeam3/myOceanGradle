package com.example.myoceanproject.repository.community.post;

import com.example.myoceanproject.domain.*;
import com.example.myoceanproject.entity.CommunityLike;
import com.example.myoceanproject.entity.CommunityPost;
import com.example.myoceanproject.entity.QCommunityPost;
import com.example.myoceanproject.repository.community.like.CommunityLikeRepositoryImpl;
import com.example.myoceanproject.repository.community.reply.CommunityReplyRepositoryImpl;
import com.example.myoceanproject.type.CommunityCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.myoceanproject.entity.QCommunityPost.communityPost;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommunityPostRepositoryImpl implements CommunityPostCustomRepository{
    @Autowired
    private CommunityReplyRepositoryImpl replyRepositoryImpl ;
    @Autowired
    private CommunityLikeRepositoryImpl likeRepositoryImpl ;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<CommunityPostDTO> findAllByCategory(Pageable pageable, CommunityCategory communityCategory){

        List<CommunityPostDTO> posts = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .where(communityPost.communityCategory.eq(communityCategory))
                .orderBy(communityPost.communityPostId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        posts.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });

        long total = jpaQueryFactory.selectFrom(communityPost)
                .where(communityPost.communityCategory.eq(communityCategory))
                .fetch().size();

        return new PageImpl<>(posts, pageable, total);
    }

    //    ?????? ?????? ????????????(????????? ??????)
    @Override
    public List<CommunityPostDTO> filterCommunityBoard(int page, List<String> communityCategories) {
        List<CommunityPostDTO> communityBoards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                )).from(communityPost)
                .where(
                        categoryFilter(communityCategories)
                )
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .offset(page * 10)
                .limit(10)
                .fetch();

        communityBoards.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });

        return communityBoards;
    }


    //    ?????? ?????? ????????????(???????????? ?????? ????????? ?????? ??????)
    @Override
    public List<CommunityPostDTO> filterCommunityBoard(int page, List<String> communityCategories, Long id) {
        List<CommunityPostDTO> communityBoards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                )).from(communityPost)
                .where(
                        categoryFilter(communityCategories)
                )
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .offset(page * 10)
                .limit(10)
                .fetch();

        communityBoards.stream().forEach(communityPostDTO -> {
            log.info("=============" + id); // ????????? null
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
            communityPostDTO.setCheckLike(likeRepositoryImpl.findByCommunityPostAndUser(id,communityPostDTO.getCommunityPostId()));
        });

        return communityBoards;
    }

    //    ?????? ?????? ????????????(?????? ?????? ??? ??? ?????? ??????)
    public List<CommunityPostDTO> filterCommunityBoard(int page, Long id) {
        List<CommunityPostDTO> communityBoards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                )).from(communityPost)
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .offset(page * 10)
                .limit(10)
                .fetch();

        communityBoards.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
            communityPostDTO.setCheckLike(likeRepositoryImpl.findByCommunityPostAndUser(id,communityPostDTO.getCommunityPostId()));
        });

        return communityBoards;
    }

    //    ?????? ?????? ????????????(?????? ?????? ??? ??? ?????? ??????)
    public List<CommunityPostDTO> filterCommunityBoard(int page) {
        List<CommunityPostDTO> communityBoards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                )).from(communityPost)
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .offset(page * 10)
                .limit(10)
                .fetch();

        communityBoards.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });

        return communityBoards;
    }


    @Override
    public List<CommunityPostDTO> filterCommunityBoard(List<String> communityCategories, Long userId) {
        List<CommunityPostDTO> communityBoards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                )).from(communityPost)
                .where(
                        categoryFilter(communityCategories)
                )
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .limit(10)
                .fetch();

        communityBoards.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
            communityPostDTO.setCheckLike(likeRepositoryImpl.findByCommunityPostAndUser(userId,communityPostDTO.getCommunityPostId()));
        });

        return communityBoards;
    }
    private BooleanBuilder categoryFilter(List<String> communityCategories){
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for(String communityCategory : communityCategories){
            if(communityCategory.equals("EXERCISE")){ // ???????????? ????????? ??? ??? ????????? ?????????
                booleanBuilder.or(communityPost.communityCategory.eq(CommunityCategory.EXERCISE)); //EXERCISE??? ????????????
            }
            if(communityCategory.equals("COOK")){
                booleanBuilder.or(communityPost.communityCategory.eq(CommunityCategory.COOK));
            }
            if(communityCategory.equals("MOVIE")){
                booleanBuilder.or(communityPost.communityCategory.eq(CommunityCategory.MOVIE));
            }
            if(communityCategory.equals("BOOK")){
                booleanBuilder.or(communityPost.communityCategory.eq(CommunityCategory.BOOK));
            }
            if(communityCategory.equals("COUNSELING")){
                booleanBuilder.or(communityPost.communityCategory.eq(CommunityCategory.COUNSELING));
            }
        }
        return booleanBuilder;
    }




    @Override
    public Page<CommunityPostDTO> findAllByCategory(Pageable pageable, CommunityCategory communityCategory, Criteria criteria){
        List<CommunityPostDTO> posts = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .where(communityPost.communityCategory.eq(communityCategory).and(communityPost.communityTitle.contains(criteria.getKeyword())))
                .orderBy(communityPost.communityPostId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        posts.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });
        long total = jpaQueryFactory.selectFrom(communityPost)
                .where(communityPost.communityCategory.eq(communityCategory).and(communityPost.communityTitle.contains(criteria.getKeyword())))
                .fetch().size();

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<CommunityPostDTO> findAll(Pageable pageable){
        List<CommunityPostDTO> posts = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .where(communityPost.communityCategory.ne(CommunityCategory.COUNSELING))
                .orderBy(communityPost.communityPostId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        posts.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });
        long total = jpaQueryFactory.selectFrom(communityPost)
                .where(communityPost.communityCategory.ne(CommunityCategory.COUNSELING))
                .fetch().size();

        return new PageImpl<>(posts, pageable, total);
    }


    //  ???????????? ????????? ????????????
    @Override
    public List<CommunityPostDTO> findAllByList(Long userId){
        List<CommunityPostDTO> posts = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .limit(10)
                .fetch();

        posts.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
            communityPostDTO.setCheckLike(likeRepositoryImpl.findByCommunityPostAndUser(userId,communityPostDTO.getCommunityPostId()));
        });

        return posts;
    }


    //  ????????? ??? ??? ????????? ?????? ???
    public List<CommunityPostDTO> findAllByListWithoutLogin(){
        List<CommunityPostDTO> posts = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .limit(10)
                .fetch();

        posts.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });

        return posts;
    }

    @Override
    public Page<CommunityPostDTO> findAll(Pageable pageable, Criteria criteria){
//        ???????????? ?????? ??????
        List<CommunityPostDTO> posts = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .where(communityPost.communityTitle.contains(criteria.getKeyword()).and(communityPost.communityCategory.ne(CommunityCategory.COUNSELING)))
                .orderBy(communityPost.communityPostId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        posts.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });
        long total = jpaQueryFactory.selectFrom(communityPost)
                .where(communityPost.communityTitle.contains(criteria.getKeyword()).and(communityPost.communityCategory.ne(CommunityCategory.COUNSELING)))
                .fetch().size();

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public void deleteByPost(CommunityPost post){
//        ????????? ????????? entity??? ???????????? ??????
        List<CommunityLike> likes = likeRepositoryImpl.findByCommunityPost(post);
        List<CommunityReplyDTO> replies = replyRepositoryImpl.findByCommunityPost(post);

//        ?????? ?????????????????? ???????????? ??????????
        if(!likes.isEmpty()) {
//        ?????? ?????? ???????????? ???????????? ???????????? ????????? ????????? ?????? ????????????.
            likeRepositoryImpl.deleteByCommunityPost(post);
        }
//        ????????? ??????????
        if(!replies.isEmpty()) {
//        ?????? ?????? ???????????? ???????????? ???????????? ????????? ????????? ?????? ????????????.
            replyRepositoryImpl.deleteByCommunityPost(post);
        }

    }

    @Override
    public Integer countPostByUser(Long userId){
        return Math.toIntExact(jpaQueryFactory.select(communityPost.communityPostId.count())
                .from(communityPost)
                .where(communityPost.user.userId.eq(userId))
                .fetchFirst());
    }

    public CommunityPostDTO findByCommunityId(Long communityPostId){
        return jpaQueryFactory.select(new QCommunityPostDTO(
                communityPost.communityPostId,
                communityPost.user.userId,
                communityPost.user.userNickname,
                communityPost.user.userFileName,
                communityPost.user.userFilePath,
                communityPost.user.userFileSize,
                communityPost.user.userFileUuid,
                communityPost.communityCategory,
                communityPost.communityTitle,
                communityPost.communityContent,
                communityPost.communityFilePath,
                communityPost.communityFileName,
                communityPost.communityFileUuid,
                communityPost.communityFileSize,
                communityPost.communityViewNumber,
                communityPost.communityLikeNumber,
                communityPost.createDate,
                communityPost.updatedDate
        )).from(communityPost).where(communityPost.communityPostId.eq(communityPostId)).fetchOne();
    }



    // ???????????????
    // ????????? 10?????? ????????????, ???????????? ??? ??? ?????? ????????? ????????? 10?????? ?????????
    public List<CommunityPostDTO> selectScrollBoards(int page){
        List<CommunityPostDTO> boards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .offset(page * 10)
                .limit(10)
                .fetch();
        boards.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });
        return boards;
    }

    // ???????????????(????????????)
    // ????????? 10?????? ????????????, ???????????? ??? ??? ?????? ????????? ????????? 10?????? ?????????
    public List<CommunityPostDTO> selectScrollBoards(int page, Long userId){
        List<CommunityPostDTO> boards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .offset(page * 10)
                .limit(10)
                .fetch();
        boards.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
            communityPostDTO.setCheckLike(likeRepositoryImpl.findByCommunityPostAndUser(userId,communityPostDTO.getCommunityPostId()));
        });
        return boards;
    }

    public CommunityPostDTO findAllByDashboard(){
        List<CommunityPostDTO> posts = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                ))
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .offset(0)
                .limit(7)
                .fetch();

        CommunityPostDTO postDTO = new CommunityPostDTO();
        postDTO.setPostList(posts);
        return postDTO;
    }

    @Override
    public List<CommunityPostDTO> filterCommunityBoard(List<String> communityCategories) {
        List<CommunityPostDTO> communityBoards = jpaQueryFactory.select(new QCommunityPostDTO(
                        communityPost.communityPostId,
                        communityPost.user.userId,
                        communityPost.user.userNickname,
                        communityPost.user.userFileName,
                        communityPost.user.userFilePath,
                        communityPost.user.userFileSize,
                        communityPost.user.userFileUuid,
                        communityPost.communityCategory,
                        communityPost.communityTitle,
                        communityPost.communityContent,
                        communityPost.communityFilePath,
                        communityPost.communityFileName,
                        communityPost.communityFileUuid,
                        communityPost.communityFileSize,
                        communityPost.communityViewNumber,
                        communityPost.communityLikeNumber,
                        communityPost.createDate,
                        communityPost.updatedDate
                )).from(communityPost)
                .where(
                        categoryFilter(communityCategories)
                )
                .from(communityPost)
                .orderBy(communityPost.createDate.desc())
                .limit(10)
                .fetch();

        communityBoards.stream().forEach(communityPostDTO -> {
            communityPostDTO.setCommunityReplyCount(replyRepositoryImpl.countReplyByCommunityPost(communityPostDTO.getCommunityPostId()));
        });

        return communityBoards;
    }
}
