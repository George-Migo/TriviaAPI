module TriviaAPI {
	exports Exception;
	exports services;
	exports model;
	exports model.triviadb;

	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;
	
	opens model.triviadb to com.fasterxml.jackson.databind;
}