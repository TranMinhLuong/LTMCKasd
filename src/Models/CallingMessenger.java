package Models;

import java.awt.Image;
import java.io.Serializable;

public class CallingMessenger implements Serializable {
	private byte[] img;

	public CallingMessenger(byte[] img) {
		super();
		this.img = img;
	}

	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}
	
}
