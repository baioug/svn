package cn.phoniex.ssg.engine;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.R.raw;
import cn.phoniex.ssg.domain.ContactInfo;

import android.R.integer;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactInfoProvider {

	private Context context;

	public ContactInfoProvider(Context context) {
		this.context = context;
	}
	public  List<ContactInfo> getAllContact()
	{
		//raw�����������_id display_name ����û���ֻ���
		//data�������ֻ���
		ContentResolver resolver = context.getContentResolver();
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		ContactInfo info;
		Uri Rawuri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dateUri = Uri.parse("content://com.android.contacts/data");
		Cursor Rawcursor = resolver.query(Rawuri, null, null, null, null);
		while (Rawcursor.moveToNext()) {
			info = new ContactInfo();
			
			String id = Rawcursor.getString(Rawcursor.getColumnIndex("_id"));
			String name =	Rawcursor.getString(Rawcursor.getColumnIndex("display_name"));
			info.setName(name);
			Cursor datacursor = resolver.query(dateUri, null, "raw_contact_id=?", new String[]{"id"}, null);
			while(datacursor.moveToNext())
			{
				String type = datacursor.getString(datacursor.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/phone_v2".equals(type)) {
					String phone = datacursor.getString(datacursor.getColumnIndex("data1"));
					info.setPhone(phone);
				}
				
			}
			datacursor.close();
			infos.add(info);
			info = null;
		}
		Rawcursor.close();
		
		return infos;
		
	}
	
	public List<ContactInfo> getContactInfos()
	{
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		ContactInfo info;
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, 
				null, null, null,
				null);
		//ContactsContract.Contacts.DISPLAY_NAME + "COLLATE LOCALIZED ASC")
//		if (cursor.getCount()>0) {
	//		
	//	}
		if (cursor.moveToFirst()) {
			int idColum = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			int displayNameColum = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			
			do {
				//�ж��Ƿ���ں���
				if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) >0 ) {
					{
						String contactid = cursor.getString(idColum);
						String displayname = cursor.getString(displayNameColum);
						//���� contact_id ��ѯ�ֻ��� ��һ���˿��ܴ��ڶ������
						Cursor phoCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
								null, 
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = "+contactid, 
								null, null);
						//Ϊʲô��������ĸ�ʽ�� 
						//ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?",  new String[]{ contactid}
						if (phoCursor.moveToFirst()) {
							do {
								int noColum = phoCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
								String phonenumber = phoCursor.getString(noColum);
								int typeColum = phoCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
								String phoType = phoCursor.getString(typeColum);
								//ͬһ���˵�ÿһ�����붼������ӵ� list��
								info = new ContactInfo();
								info.setName(displayname);
								info.setPhone(phonenumber);
								infos.add(info);
								info =null;
								
							} while (phoCursor.moveToNext());
						}
						
					}
					
					
					/*
					 
					// ��ȡ����ϵ������
				Cursor emails = getContentResolver().query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				if (emails.moveToFirst()) {
					do {
						// �������еĵ绰����
						String emailType = emails
								.getString(emails
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
						String emailValue = emails
								.getString(emails
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						
						Log.i("emailType", emailType);
						Log.i("emailValue", emailValue);
					} while (emails.moveToNext());
				}

				// ��ȡ����ϵ��IM
				Cursor IMs = getContentResolver().query(
						Data.CONTENT_URI,
						new String[] { Data._ID, Im.PROTOCOL, Im.DATA },
						Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
								+ Im.CONTENT_ITEM_TYPE + "'",
						new String[] { contactId }, null);
				if (IMs.moveToFirst()) {
					do {
						String protocol = IMs.getString(IMs
								.getColumnIndex(Im.PROTOCOL));
						String date = IMs
								.getString(IMs.getColumnIndex(Im.DATA));
						Log.i("protocol", protocol);
						Log.i("date", date);
					} while (IMs.moveToNext());
				}

				// ��ȡ����ϵ�˵�ַ
				Cursor address = getContentResolver()
						.query(
								ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ " = " + contactId, null, null);
				if (address.moveToFirst()) {
					do {
						// �������еĵ�ַ
						String street = address
								.getString(address
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
						String city = address
								.getString(address
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
						String region = address
								.getString(address
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
						String postCode = address
								.getString(address
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
						String formatAddress = address
								.getString(address
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
						Log.i("street", street);
						Log.i("city", city);
						Log.i("region", region);
						Log.i("postCode", postCode);
						Log.i("formatAddress", formatAddress);
					} while (address.moveToNext());
				}

				// ��ȡ����ϵ����֯
				Cursor organizations = getContentResolver().query(
						Data.CONTENT_URI,
						new String[] { Data._ID, Organization.COMPANY,
								Organization.TITLE },
						Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
								+ Organization.CONTENT_ITEM_TYPE + "'",
						new String[] { contactId }, null);
				if (organizations.moveToFirst()) {
					do {
						String company = organizations.getString(organizations
								.getColumnIndex(Organization.COMPANY));
						String title = organizations.getString(organizations
								.getColumnIndex(Organization.TITLE));
						Log.i("company", company);
						Log.i("title", title);
					} while (organizations.moveToNext());
				}

				// ��ȡ��ע��Ϣ
				Cursor notes = getContentResolver().query(
						Data.CONTENT_URI,
						new String[] { Data._ID, Note.NOTE },
						Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
								+ Note.CONTENT_ITEM_TYPE + "'",
						new String[] { contactId }, null);
				if (notes.moveToFirst()) {
					do {
						String noteinfo = notes.getString(notes
								.getColumnIndex(Note.NOTE));
						Log.i("noteinfo", noteinfo);
					} while (notes.moveToNext());
				}

				// ��ȡnickname��Ϣ
				Cursor nicknames = getContentResolver().query(
						Data.CONTENT_URI,
						new String[] { Data._ID, Nickname.NAME },
						Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
								+ Nickname.CONTENT_ITEM_TYPE + "'",
						new String[] { contactId }, null);
				if (nicknames.moveToFirst()) {
					do {
						String nickname_ = nicknames.getString(nicknames
								.getColumnIndex(Nickname.NAME));
						Log.i("nickname_", nickname_);
					} while (nicknames.moveToNext());
				}

					 */

				}
				
				
			} while (cursor.moveToNext());
		}

		
		return infos;
		
	}
	
	
}
