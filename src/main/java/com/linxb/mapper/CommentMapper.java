package com.linxb.mapper;

import com.linxb.bean.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);
    int selectCountByEntity(int entityType,int entityId);
    // 添加评论
    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}

