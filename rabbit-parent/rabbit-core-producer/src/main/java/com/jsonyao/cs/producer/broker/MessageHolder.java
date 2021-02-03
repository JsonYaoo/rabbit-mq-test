package com.jsonyao.cs.producer.broker;

import com.google.common.collect.Lists;
import com.jsonyao.cs.api.Message;

import java.util.List;

/**
 * ThreadLocal容器: 用于批量发送消息
 */
public class MessageHolder {

    private List<Message> messages = Lists.newArrayList();

    // @SuppressWarnings屏蔽一些无关紧要的警告, 使开发者能看到一些他们真正关心的警告
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ThreadLocal<MessageHolder> holder = new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return new MessageHolder();
        }
    };

    /**
     * 向当前线程批量消息集合中添加消息
     * @param message
     */
    public static void add(Message message){
        holder.get().messages.add(message);
    }

    /**
     * 清除ThreadLocal副本, 获取批量消息
     * @return
     */
    public static List<Message> clear(){
        List<Message> tmp = Lists.newArrayList(holder.get().messages);
        holder.remove();// ThreadLocal不用时一定要清除键, 防止内存泄露
        return tmp;
    }
}
