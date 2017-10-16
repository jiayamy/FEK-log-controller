package testSink;

import java.util.regex.Pattern;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSink extends AbstractSink implements Configurable{
	
	Logger log = LoggerFactory.getLogger(TestSink.class);
	
	static String patten = "cost time";
	
	public void start(){
		log.info("====testSink start=====");
	}
	
	public Status process() throws EventDeliveryException {
		log.info("====testSink process====");
		Status status = null;
		Channel ch = getChannel();
		Transaction txn = ch.getTransaction();
		txn.begin();
		try {
			while(true){
				Event event = ch.take();
				if(event != null){
					try {
						String content = new String(event.getBody(), "UTF-8");
//						log.info(content);
//						InetAddress ia = InetAddress.getLocalHost();
//						String host = ia.getHostName();
//						JSONObject jsonObject = JSONObject.fromObject(content);
//						SinkModel sinkModel = (SinkModel) JSONObject.toBean(jsonObject, SinkModel.class);
//						sinkModel.setHostName(host);
//						log.info(sinkModel.toString());
//						JSONObject jo = JSONObject.fromObject(sinkModel);
//						String json = jo.toString();
//						event.setBody(json.getBytes());
//						log.info(event.getBody().toString());
					} catch (Exception e) {
						log.info("====="+e.getMessage()+"=====");
						break;
					}
				}else{
					break;
				}
			}
			status = Status.READY;
			txn.commit();
		} catch (Exception e) {
			log.info("===prcess error : "+e.getMessage()+"====");
			txn.rollback();  
			status = Status.BACKOFF;  
		} finally {
			txn.close();
		}
		return status;
	}
	
	public void stop(){
		log.info("====stop testSink====");
	}
	
	public void configure(Context arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		System.out.println("1111");
		String str = "[json_server][cmc-unkownIp-22816500-unkownUser][INFO ](DBNameReplace.java:133) - 2017-09-05 16:47:31,113 replace db object name cost time :0 ms"	;
		String pat = "(.*cost time\\s*):(\\d+)(\\s*\\w*)";
		String[] strs = str.split(pat);
		System.out.println(strs[0]);
	}
	
}
