package com.budgetBook.common;

public interface HashingEncoder {
	// 같은 규격의 메소드들은 이 규격을 사용하도록 규격 만들기
	public String encode(String message);
}
