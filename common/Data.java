package common;

import java.util.Date;
import java.util.Base64;

public class Data {
    public Type type;
    public Date date;
    public String source;
    public String destination;
    public byte[] content;
    
    public Data(Type type_, Date date_, String source_, String destination_, byte[] content_) {
    	type = type_;
    	date = date_;
    	source = source_;
    	destination = destination_;
    	content = content_;
    }
    
    public Data(byte[] buf, int pos, int len) {
    	byte[] temp = buf;
    	buf = new byte[len];
    	for(int i=0; i<len; ++i)
    		buf[i] = temp[pos+i];
    	
    	buf = Base64.getDecoder().decode(buf);
    	buf = Cipher.decrypt(buf, 0, len);
    	
    	type = Type.values()[buf[pos++]];
    	
    	long dat = 0;
    	for(int i=0; i<8; ++i)
    		dat |= (((long) buf[pos++] + 256) % 256 << (8*i));
    	date = new Date(dat);
    	
    	source = new String(buf, pos+1, buf[pos]);
    	pos += (buf[pos]+1);
    	
    	destination = new String(buf, pos+1, buf[pos]);
    	pos += (buf[pos]+1);
    	
    	content = new byte[len-pos];
    	for(int i=0; i<content.length; ++i)
    		content[i] = buf[pos++];
    }
    
    public byte[] getBytes() {
    	byte typ = (byte) type.ordinal();
    	long dat = date.getTime();
    	byte[] src = source.getBytes();
    	byte[] dst = destination.getBytes();
    	
    	int len = 11+src.length+dst.length+content.length;
    	byte[] buf = new byte[len];
    	
    	int pos = 0;
    	buf[pos++] = typ;
    	for(int i=0; i<8; ++i)
    		buf[pos++] = (byte) ((dat >> (8*i)) & 0xFF);
    	
    	buf[pos++] = (byte) src.length;
    	for(int i=0; i<src.length; ++i)
    		buf[pos++] = src[i];
    	
    	buf[pos++] = (byte) dst.length;
    	for(int i=0; i<dst.length; ++i)
    		buf[pos++] = dst[i];
    	
    	for(int i=0; i<content.length; ++i)
    		buf[pos++] = content[i];
    	
    	buf = Cipher.encrypt(buf, 0, len);
    	buf = Base64.getEncoder().encode(buf);
    	return buf;
    }
    
    public enum Type {
    	TEXT, IMAGE, VIOCE, COMMAND, MESSAGE
    }
}