package com.linxb.mapper;

import com.linxb.bean.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface DiscussPostMapper {
    /**
     * @param userId 看mapper就知道了
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset")int offset, @Param("limit")int limit);
    //如果需要动态拼接条件（<if>里使用）并且这个方法有且只有一个参数需要用@Param起别名
    //@Param用于给参数取别名
    int selectDiscussPostRows(@Param("userId") int userId);
    // 插入帖子内容
    int insertDiscussPost(DiscussPost discussPost);
    // 查询贴子内容
    DiscussPost selectDiscussPostById(int id);
    // 增加贴子回复数量
    int updateCommentCount(int id,int commentCount);
}
