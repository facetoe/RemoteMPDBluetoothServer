package org.a0z.mpdlocal;




public class Genre extends Item {

	private final String name;
	private final String sort;

	public Genre(String name) {
		this.name = name;
		sort = null;
	}

	public String getName() {
		return name;
	}

    @Override
    public int compareTo(Item o) {
        if (o instanceof Genre) {
            Genre oa = (Genre) o;
                        /*
                         * if (isVa && !oa.isVa) { return -1; } if (!isVa && oa.isVa) {
                         * return 1; }
                         */
            return sort().compareToIgnoreCase(oa.sort());
        }

        return super.compareTo(o);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Genre) && ((Genre) o).name.equals(name);
    }

	public String sort() {
		return null == sort ? name : sort;
	}
}
