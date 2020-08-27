package com.yd.test.components.mp.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 
 * 加东软默认逻辑，枚举类取值都为大小，数据库和接口调用都为小写，修改从数据库返回枚举类的默认转换逻辑。即为：优先匹配全大小，数据库取值，全小写
 * 见getNullableResult方法
 * @author <a href="mailto:he.jf@neusoft.com">he.jf</a>
 * @version $Revision 1.0 $ 2019年2月27日 上午8:43:12
 */
public class EnumTypeHandlerTest<E extends Enum<E>> extends BaseTypeHandler<E> {

  private final Class<E> type;

  public EnumTypeHandlerTest(Class<E> type) {
    if (type == null) {
      throw new IllegalArgumentException("Type argument cannot be null");
    }
    this.type = type;
  }

  /***
   * 
   * setNonNullParameter 增加东软逻辑，枚举入库使用小写
   * @param ps
   * @param i
   * @param parameter
   * @param jdbcType
   * @throws SQLException 
   * @see org.apache.ibatis.type.BaseTypeHandler#setNonNullParameter(java.sql.PreparedStatement, int, java.lang.Object, org.apache.ibatis.type.JdbcType)
   */
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
    if (jdbcType == null) {
      ps.setString(i, parameter.name().toLowerCase());
    } else {
      ps.setObject(i, parameter.name().toLowerCase(), jdbcType.TYPE_CODE); // see r3589
    }
  }

  /***
   * 
   * getNullableResult 增加东软默认逻辑，枚举类取值都为大小，数据库和接口调用都为小写，修改从数据库返回枚举类的默认转换逻辑。即为：优先匹配全大小，数据库取值，全小写
   * @param rs
   * @param columnName
   * @return
   * @throws SQLException 
   * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.ResultSet, java.lang.String)
   */
  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String s = rs.getString(columnName);
    try {
        return s == null ? null : Enum.valueOf(type, s.toUpperCase());
    }catch (Exception e) {
        try {
            return s == null ? null : Enum.valueOf(type, s);
        }catch (Exception e2) {
            return s == null ? null : Enum.valueOf(type, s.toLowerCase());
        }
      
    }
   
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String s = rs.getString(columnIndex);
    return s == null ? null : Enum.valueOf(type, s);
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String s = cs.getString(columnIndex);
    return s == null ? null : Enum.valueOf(type, s);
  }
}