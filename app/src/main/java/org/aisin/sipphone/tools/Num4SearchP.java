package org.aisin.sipphone.tools;

import java.util.ArrayList;
import java.util.TreeSet;


public class Num4SearchP {

	public static TreeSet<String> getN4PResult(String Str) {
		if (Str == null || "".equals(Str) || !Check_format.Check_num(Str)
				|| Str.length() > 5) {
			return null;
		} else {
			TreeSet<String> list = new TreeSet<String>();
			ArrayList<TreeSet<String>> lists = null;
			TreeSet<String> list_temp = new TreeSet<String>();
			char[] cars = Str.toCharArray();
			for (int i = 0; i < cars.length; i++) {
				char car = cars[i];
				switch (car) {
				case '2':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_a = new TreeSet<String>();
						TreeSet<String> list_b = new TreeSet<String>();
						TreeSet<String> list_c = new TreeSet<String>();
						list_a.add("a");
						list_b.add("b");
						list_c.add("c");
						lists.add(list_a);
						lists.add(list_b);
						lists.add(list_c);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "a");
								list_temp.add(str + "b");
								list_temp.add(str + "c");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				case '3':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_d = new TreeSet<String>();
						TreeSet<String> list_e = new TreeSet<String>();
						TreeSet<String> list_f = new TreeSet<String>();
						list_d.add("d");
						list_e.add("e");
						list_f.add("f");
						lists.add(list_d);
						lists.add(list_e);
						lists.add(list_f);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "d");
								list_temp.add(str + "e");
								list_temp.add(str + "f");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				case '4':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_g = new TreeSet<String>();
						TreeSet<String> list_h = new TreeSet<String>();
						TreeSet<String> list_i = new TreeSet<String>();
						list_g.add("g");
						list_h.add("h");
						list_i.add("i");
						lists.add(list_g);
						lists.add(list_h);
						lists.add(list_i);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "g");
								list_temp.add(str + "h");
								list_temp.add(str + "i");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				case '5':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_j = new TreeSet<String>();
						TreeSet<String> list_k = new TreeSet<String>();
						TreeSet<String> list_l = new TreeSet<String>();
						list_j.add("j");
						list_k.add("k");
						list_l.add("l");
						lists.add(list_j);
						lists.add(list_k);
						lists.add(list_l);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "j");
								list_temp.add(str + "k");
								list_temp.add(str + "l");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				case '6':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_m = new TreeSet<String>();
						TreeSet<String> list_n = new TreeSet<String>();
						TreeSet<String> list_o = new TreeSet<String>();
						list_m.add("m");
						list_n.add("n");
						list_o.add("o");
						lists.add(list_m);
						lists.add(list_n);
						lists.add(list_o);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "m");
								list_temp.add(str + "n");
								list_temp.add(str + "o");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				case '7':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_p = new TreeSet<String>();
						TreeSet<String> list_q = new TreeSet<String>();
						TreeSet<String> list_r = new TreeSet<String>();
						TreeSet<String> list_s = new TreeSet<String>();
						list_p.add("p");
						list_q.add("q");
						list_r.add("r");
						list_s.add("s");
						lists.add(list_p);
						lists.add(list_q);
						lists.add(list_r);
						lists.add(list_s);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "p");
								list_temp.add(str + "q");
								list_temp.add(str + "r");
								list_temp.add(str + "s");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				case '8':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_t = new TreeSet<String>();
						TreeSet<String> list_u = new TreeSet<String>();
						TreeSet<String> list_v = new TreeSet<String>();
						list_t.add("t");
						list_u.add("u");
						list_v.add("v");
						lists.add(list_t);
						lists.add(list_u);
						lists.add(list_v);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "t");
								list_temp.add(str + "u");
								list_temp.add(str + "v");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				case '9':
					if (lists == null) {
						lists = new ArrayList<TreeSet<String>>();
						TreeSet<String> list_w = new TreeSet<String>();
						TreeSet<String> list_x = new TreeSet<String>();
						TreeSet<String> list_y = new TreeSet<String>();
						TreeSet<String> list_z = new TreeSet<String>();
						list_w.add("w");
						list_x.add("x");
						list_y.add("y");
						list_z.add("z");
						lists.add(list_w);
						lists.add(list_x);
						lists.add(list_y);
						lists.add(list_z);
					} else {
						for (TreeSet<String> list_m : lists) {
							list_temp.clear();
							for (String str : list_m) {
								list_temp.add(str + "w");
								list_temp.add(str + "x");
								list_temp.add(str + "y");
								list_temp.add(str + "z");
							}
							list_m.clear();
							list_m.addAll(list_temp);
						}
					}
					break;
				}
			}
			if (lists == null) {
				return null;
			}
			for (TreeSet<String> ll : lists) {
				list.addAll(ll);
			}
			list_temp.clear();
			list_temp = null;
			lists.clear();
			lists = null;
			return list;
		}
	}

}
