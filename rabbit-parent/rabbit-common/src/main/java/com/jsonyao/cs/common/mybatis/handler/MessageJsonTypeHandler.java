package com.jsonyao.cs.common.mybatis.handler;

import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.common.util.FastJsonConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mybatis序列化助手类
 */
public class MessageJsonTypeHandler extends BaseTypeHandler<Message> {

    /**
     * 为第i个参数设置非空属性
     * @param preparedStatement
     * @param i
     * @param message
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Message message, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, FastJsonConvertUtil.convertObjectToJSON(message));
    }

    /**
     * 根据列名获取非空集合中的值
     * @param resultSet
     * @param columnName
     * @return
     * @throws SQLException
     */
    @Override
    public Message getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String value = resultSet.getString(columnName);
        if(StringUtils.isNotBlank(value)){
           return FastJsonConvertUtil.convertJSONToObject(value, Message.class);
        }
        return null;
    }

    /**
     * 根据列索引获取非空集合中的值
     * @param resultSet
     * @param index
     * @return
     * @throws SQLException
     */
    @Override
    public Message getNullableResult(ResultSet resultSet, int index) throws SQLException {
        String value = resultSet.getString(index);
        if(StringUtils.isNotBlank(value)){
            return FastJsonConvertUtil.convertJSONToObject(value, Message.class);
        }
        return null;
    }

    /**
     * 根据列索引获取回调Statement中的值
     * @param callableStatement
     * @param index
     * @return
     * @throws SQLException
     */
    @Override
    public Message getNullableResult(CallableStatement callableStatement, int index) throws SQLException {
        String value = callableStatement.getString(index);
        if(StringUtils.isNotBlank(value)){
            return FastJsonConvertUtil.convertJSONToObject(value, Message.class);
        }
        return null;
    }
}
