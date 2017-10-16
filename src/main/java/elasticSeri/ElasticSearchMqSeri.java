package elasticSeri;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.elasticsearch.ContentBuilderUtil;
import org.apache.flume.sink.elasticsearch.ElasticSearchEventSerializer;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
* Serialize flume events into the same format LogStash uses</p>
*
* This can be used to send events to ElasticSearch and use clients such as
* Kabana which expect Logstash formated indexes
*
* <pre>
* {
*    "@timestamp": "2010-12-21T21:48:33.309258Z",
*    "@tags": [ "array", "of", "tags" ],
*    "@type": "string",
*    "@source": "source of the event, usually a URL."
*    "@source_host": ""
*    "@source_path": ""
*    "@fields":{
*       # a set of fields for this event
*       "user": "jordan",
*       "command": "shutdown -r":
*     }
*     "@message": "the original plain-text message"
*   }
* </pre>
*
* If the following headers are present, they will map to the above logstash
* output as long as the logstash fields are not already present.</p>
*
* <pre>
*  timestamp: long -> @timestamp:Date
*  host: String -> @source_host: String
*  src_path: String -> @source_path: String
*  type: String -> @type: String
*  source: String -> @source: String
* </pre>
*
* @see https
*      ://github.com/logstash/logstash/wiki/logstash%27s-internal-message-
*      format
*/
public class ElasticSearchMqSeri implements
   ElasticSearchEventSerializer {

	private static String[] ignoreStrs = new String[]{"msg","qurity"};
	
	private static List<String> ignores = new ArrayList<String>();
	
 public XContentBuilder getContentBuilder(Event event) throws IOException {
   XContentBuilder builder = jsonBuilder().startObject();
   init();
   appendBody(builder, event);
   appendHeaders(builder, event);
   return builder;
 }
 
 private void init()	{
	 for(String str : ignoreStrs){
		 ignores.add(str);
	 }
 }

 private void appendBody(XContentBuilder builder, Event event)
     throws IOException, UnsupportedEncodingException {
   byte[] body = event.getBody();
   String msg = new String(body);
   String patten = "(.*cost time\\s*(:|：))\\s*(\\d+)\\s*\\w*.*";
	String str = msg.replaceFirst(patten, "$3");
	try {
		builder.field("@cost-time", Long.parseLong(str));
	} catch (Exception e) {

	}
//	ContentBuilderUtil.appendField(builder, "@cost-time", str.getBytes());
   ContentBuilderUtil.appendField(builder, "@message", body);
 }

 private void appendHeaders(XContentBuilder builder, Event event)
     throws IOException {
   Map<String, String> headers = Maps.newHashMap(event.getHeaders());

   String timestamp = headers.get("timestamp");
   if (!StringUtils.isBlank(timestamp)
       && StringUtils.isBlank(headers.get("@timestamp"))) {
     long timestampMs = Long.parseLong(timestamp);
     builder.field("@timestamp", new Date(timestampMs));
   }else{
	   builder.field("@timestamp", new Date());
   }

   String source = headers.get("source");
   if (!StringUtils.isBlank(source)
       && StringUtils.isBlank(headers.get("@source"))) {
     ContentBuilderUtil.appendField(builder, "@source",
         source.getBytes(charset));
   }

   String type = headers.get("type");
   if (!StringUtils.isBlank(type)
       && StringUtils.isBlank(headers.get("@type"))) {
     ContentBuilderUtil.appendField(builder, "@type", type.getBytes(charset));
   }

   String host = headers.get("host");
   if (!StringUtils.isBlank(host)
       && StringUtils.isBlank(headers.get("@source_host"))) {
     ContentBuilderUtil.appendField(builder, "@source_host",
         host.getBytes(charset));
   }

   String srcPath = headers.get("src_path");
   if (!StringUtils.isBlank(srcPath)
       && StringUtils.isBlank(headers.get("@source_path"))) {
     ContentBuilderUtil.appendField(builder, "@source_path",
         srcPath.getBytes(charset));
   }

   builder.startObject("@fields");
   for (String key : headers.keySet()) {
	   if(key.equals("cost-time")){
//		   builder.field("@cost-time", Long.parseLong(headers.get(key)));
		   continue;
	   }
	   if(ignores.contains(key)){
		   continue;
	   }
     byte[] val = headers.get(key).getBytes(charset);
     ContentBuilderUtil.appendField(builder, key, val);
   }
   builder.endObject();
 }

 public void configure(Context context) {
   // NO-OP...
 }

 public void configure(ComponentConfiguration conf) {
   // NO-OP...
 }
// public static void main(String[] args) {
//	String str = "[json_server][][WARN ](ApplicationFilter.java:107) - 2017-09-11 13:48:25,395 request:/cmc/reply/api/record/list cost time ： 15ms. ";
////	String patten = "(.*cost time\\s*:)\\s*(\\d+)\\s*\\w*";
//	String patten = "(.*cost time\\s*(:|：))\\s*(\\d+)\\s*\\w*.*";
//	str = str.replaceFirst(patten, "$3");
//	System.out.println(str);
//}
}
