/**
 * Copyright 2006-2014 handu.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rocketMQSink;

import java.net.InetAddress;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * 发送消息
 *
 * @author Jinkai.Ma<majinkai@handu.com>
 * @since 2015-01-16
 */
public class RocketMQSink extends AbstractSink implements Configurable {

    private static final Logger LOG = LoggerFactory.getLogger(RocketMQSink.class);

    private String topic;
    private String tag;
    private MQProducer producer;
    private String oneway;

    public void configure(Context context) {
        // 获取配置项
        topic = context.getString(RocketMQSinkUtil.TOPIC_CONFIG, RocketMQSinkUtil.TOPIC_DEFAULT);
        tag = context.getString(RocketMQSinkUtil.TAG_CONFIG, RocketMQSinkUtil.TAG_DEFAULT);
        // 初始化Producer
        producer = Preconditions.checkNotNull(RocketMQSinkUtil.getProducer(context));
        oneway = context.getString(RocketMQSinkUtil.ONEWAY_CONFIG, RocketMQSinkUtil.ONEWAY_DEFAULT);
    }

    public Status process() throws EventDeliveryException {
        Channel channel = getChannel();
        Transaction tx = channel.getTransaction();
        String str="";
        try {
            tx.begin();
            Event event = channel.take();
            if (event == null || event.getBody() == null || event.getBody().length == 0) {
                tx.commit();
                return Status.READY;
            }
            if(event.getBody()!=null)
            str = new String(event.getBody());
            InetAddress ia = InetAddress.getLocalHost();
        	String host = ia.getHostName();
            // 发送消息
        	if("1".equals(oneway)){
        		producer.sendOneway(new Message(topic, tag,host, event.getBody()));
        	}else{
        		SendResult sendResult = producer.send(new Message(topic, tag,host, event.getBody()));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SendResult={}, Message={}", sendResult, new String(event.getBody()));
                }
        	}
            tx.commit();
            return Status.READY;
        } catch (Exception e) {
            LOG.error(new String(str)+",RocketMQSink send message exception", e);
            try {
            	 tx.commit();
                 return Status.READY;
            } catch (Exception e2) {
                LOG.error("Rollback exception", e2);
            }
            return Status.BACKOFF;
        } finally {
            tx.close();
        }
    }

    @Override
    public synchronized void start() {
        try {
            // 启动Producer
            producer.start();
        } catch (MQClientException e) {
            LOG.error("RocketMQSink start producer failed", e);
            Throwables.propagate(e);
        }
        super.start();
    }

    @Override
    public synchronized void stop() {
        // 停止Producer
        producer.shutdown();
        super.stop();
    }
}
