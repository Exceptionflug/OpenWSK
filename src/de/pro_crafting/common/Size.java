package de.pro_crafting.common;

public class Size {
	private int height;
	private int width;
	private int depth;
	
	public Size(int width, int height, int depth) {
		this.height = height;
		this.width = width;
		this.depth = depth;
	}	

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getDepth() {
		return this.depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.depth;
		result = prime * result + this.height;
		result = prime * result + this.width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Size other = (Size) obj;
		if (this.depth != other.depth)
			return false;
		if (this.height != other.height)
			return false;
		if (this.width != other.width)
			return false;
		return true;
	}
}
