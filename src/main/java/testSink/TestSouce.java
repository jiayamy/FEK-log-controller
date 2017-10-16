package testSink;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSouce extends AbstractSource implements Configurable, PollableSource{
	
	private static final Logger log = LoggerFactory.getLogger(TestSouce.class);
	
	public Status process() throws EventDeliveryException {
		log.info("====testSink process====");
		Status status = null;
		
		return status;
	}

	public void configure(Context arg0) {
		// TODO Auto-generated method stub
		
	}

}
