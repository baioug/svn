package cn.phoniex.ssg.domain;

public class smsBackupInfo {
	private String _id; //�������
	private String thread_id;//�Ի����
	private String address;//�����˵�ַ
	
	//private String person; //�����ˣ�����һ�����־�����ϵ���б������ţ�İ����Ϊnull
	private String date;//����
	private String date_send;//�������ڣ�
	private String read;//0 δ�� 1 �Ѿ���
	private String status;
	private String type;//���� 1�ǽ��յ��ģ�2�Ƿ�����
	private String body;//��������
	
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
