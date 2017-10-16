package testSink;

import java.io.Serializable;

public class SinkModel implements Serializable{

	private static final long serialVersionUID = 1620058146336577060L;
	
	private String sex;
	
	private String userName;
	
	private Integer age;
	
	private Long timestamp;
	
	private String hostName;
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String toString() {
		return "SinkModel [sex=" + sex + ", userName=" + userName + ", age=" + age + ", timestamp=" + timestamp
				+ ", hostName=" + hostName + "]";
	}

}
