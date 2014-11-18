package com.ddk.smmp.adapter.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Tuple2<E1, E2> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final Tuple2 NULL = new Tuple2(null, null);

	public final E1 e1;
	public final E2 e2;

	public Tuple2(E1 e1, E2 e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	public static <E1, E2> Tuple2<E1, E2> tuple2(E1 e1, E2 e2) {
		return new Tuple2<E1, E2>(e1, e2);
	}

	@SuppressWarnings("unchecked")
	public static <E1, E2> Tuple2<E1, E2> nil() {
		return NULL;
	}

	public boolean isNull() {
		return this == NULL;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static <E1, E2> Map<E1, E2> toMap(boolean skipNullValues,
			Tuple2<E1, E2>... tuples) {
		Map<E1, E2> map = new LinkedHashMap<E1, E2>();
		for (Tuple2<E1, E2> t : tuples) {
			if (!skipNullValues || t.e2 != null) {
				map.put(t.e1, t.e2);
			}
		}
		return map;
	}
}