package cn.phoniex.ssg.domain;

public class smsBackupInfo {
	private String _id; //短信序号
	private String thread_id;//对话序号
	private String address;//发送人地址
	
	//private String person; //发件人，返回一个数字就是联系人列表里的序号，陌生人为null
	private String date;//日期
	private String date_send;//发送日期？
	private String read;//0 未读 1 已经读
	private String status;
	private String type;//类型 1是接收到的，2是发出的
	private String body;//短信内容
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getThread_id() {
		return thread_id;
	}
	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate_send() {
		return date_send;
	}
	public void setDate_send(String date_send) {
		this.date_send = date_send;
	}
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	

}
