package com.example.lenovo.test.model;

import java.util.List;

public class CommentList extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6196778323552324328L;
	
	private List<Comment> list;

	public List<Comment> getList() {
		return list;
	}

	public void setList(List<Comment> list) {
		this.list = list;
	}

}
