package com.sollace.util;

import com.google.common.base.Predicate;
import com.sollace.unicopia.entity.EntityCloud;

public class Util {
	public static Predicate NOT_CLOUDS = new Predicate() {
		public boolean apply(Object o) {
			return !(o instanceof EntityCloud);
		}
	};
}
