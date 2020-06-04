package com.xcy.seckill.mapper;


import com.xcy.seckill.pojo.SkUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SkUserMapper {

	@Select("select * from sk_user where id = #{id}")
	public SkUser getById(@Param("id") long id);
}
