package common;

public class Block {
	public int size;
	public int head;
	public int tail;
	public byte[] buffer;
	
	public Block(byte[] buffer_, int size_) {
		buffer = buffer_;
		size = size_;
	}
	
	public byte[] push() {
		if(head == buffer.length) return null;
		
		if(buffer.length-head > size) tail += size;
		else tail = buffer.length;
		
		byte[] buf = new byte[tail-head+1];
		
		for(int i=head; i<tail; ++i)
			buf[i-head] = buffer[i];
		buf[tail-head] = 0;
		
		head = tail;
			
		return buf;
	}
	
	public byte[] pull() {
		
		while(tail<size && buffer[tail]!=0) ++tail;
        if(tail == head) { 
        	head = (tail = 0);
        	return null;
        }
        else if(tail == size) {
        	for(int i=head; i<tail; ++i) {
        		buffer[i-head] = buffer[i];
        		buffer[i] = 0;
        	}
        	tail = tail - head;
        	head = 0;
        	return null;
        } else {
	        byte[] buf = new byte[tail-head];
	        for(int i=head; i<tail; ++i) {
	    		buf[i-head] = buffer[i];
	    		buffer[i] = 0;
	        }
	        head = (tail = tail + 1);
	        return buf;
        }
	}
}
