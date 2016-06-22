package com.main;

import java.util.ArrayList;

public class GmailSubTabData {
	
	String Author;
	String Recipient;
	String Subject;
	String viewmsgInNS;
	ArrayList<String> Attachments;
	String Direction;
	String View_in_Gmail;
	
	public ArrayList<String> getAttachments()
	{
		return Attachments;
	}
	public String getAuthor() {
		return Author;
	}
	
	public String getDirection() {
		return Direction;
	}
	public String getViewmsgInNS()
	{
		return viewmsgInNS;
	}
	public String getRecipient() {
		return Recipient;
	}
	public String getSubject() {
		return Subject;
	}
	public String getView_in_Gmail() {
		return View_in_Gmail;
	}
	public void setAttachments(ArrayList<String>  attachments)
	{
		Attachments = attachments;
	}
	public void setAuthor(String author) {
		Author = author;
	}
	public void setDirection(String direction) {
		Direction = direction;
	}
	public void setViewmsgInNS(String msgInNS)
	{
		this.viewmsgInNS = msgInNS;
	}
	public void setRecipient(String recipient) {
		Recipient = recipient;
	}
	public void setSubject(String subject) {
		Subject = subject;
	}
	public void setView_in_Gmail(String view_in_Gmail) {
		View_in_Gmail = view_in_Gmail;
	}
	
	
}
