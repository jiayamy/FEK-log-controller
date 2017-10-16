package testSink;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

public class TestInterceptor implements Interceptor{
	
	private static final Logger log = LoggerFactory.getLogger(TestInterceptor.class);
	
	static String Patten = "cost time";
	
	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	public Event intercept(Event event) {
		String body = new String(event.getBody(), Charsets.UTF_8);
		int index = body.indexOf(Patten);
		if(index != -1){
			Map<String, Object> costMap = new HashMap<String, Object>();
			String costStr = body.substring(index);
			String msgStr = body.substring(0, index);
			String[] coStrs = costStr.split(":");
			if(coStrs != null && coStrs.length > 1){
				String msStr = coStrs[1].replaceAll("[^(0-9)]", "");;
				costMap.put("cost_time", Long.parseLong(msStr));
			}
			costMap.put("msg", msgStr);
			event.setBody(JSON.toString(costMap).getBytes());
//			event.setHeaders(costMap);
		}
		return event;
	}

	public List<Event> intercept(List<Event> events) {
		List<Event> outs = new ArrayList<Event>();
		for(Event event : events){
			Event out = intercept(event);
			if(out != null){
				outs.add(out);
			}
		}
		return outs;
	}
	
	public static class Builder implements Interceptor.Builder {  
        //使用Builder初始化Interceptor  
        public Interceptor build() {  
            return new TestInterceptor();  
        }  
  
        public void configure(Context context) {  
  
        }  
    }  
}
