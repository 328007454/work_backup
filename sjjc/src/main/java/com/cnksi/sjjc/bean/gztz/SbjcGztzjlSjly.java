package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = SbjcGztzjlSjly._TABLE_NAME_)
public class SbjcGztzjlSjly{
	public static final String _TABLE_NAME_="sbjc_gztzjl_sjly";

	public static final String _PK_NAME_ = "id";

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id  ;
	
	/**与自段名称相同*/
	public static final String TYPE = "type";
	@Column(name = TYPE)
	public String type  ;
	
	/**type的对应的名称*/
	public static final String NAME = "name";
	@Column(name = NAME)
	public String name  ;
	
	/***/
	public static final String JSON_V = "json_v";
	@Column(name = JSON_V)
	public String jsonV  ;
	
	/***/
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public int dlt  =0 ;
	
	
}
