import java.util.ArrayList;

abstract class Formula
{
	//タブローの規則が適用されたかどうかのフラグ
	private boolean used=false;
	
	public void use()
	{
		used = true;
	}

	public boolean isUsed()
	{
		return used;
	}

	abstract public void assignValue(String var, String val);
	abstract Formula copy();
}

class Proposition extends Formula
{
	private String name;

	public Proposition(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return name;
	}

	public boolean equals(Object o)
	{
		if(o instanceof Proposition)
		{
			Proposition p = (Proposition)o;
			return name.equals(p.getName());
		}
		else
		{
			return false;
		}
	}

	public void assignValue(String var, String val)
	{
	}

	Formula copy()
	{
		return new Proposition(name);
	}

}

class Predicate extends Formula
{
	private String name;
	ArrayList<String> subjects;

	public Predicate(String name, ArrayList<String> subjects)
	{
		this.name = name;
		this.subjects = subjects;
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<String> getSubjects()
	{
		return subjects;
	}

	public void assignValue(String var, String val)
	{
		int i;
		for(i=0; i < subjects.size(); i++)
		{
			if(subjects.get(i).equals(var))
				subjects.set(i, val);
		}
	}

	public String toString()
	{
		StringBuffer buff = new StringBuffer();
		String s;
		for(int i=0; i < subjects.size(); i++)
		{
			buff.append(subjects.get(i) + ",");
		}
		if(buff.length() > 0)
		{
			s = buff.substring(0, buff.length()-1);
		}
		else
		{
			s = "";
		}
		return name + "(" + s + ")";
	}

	public boolean equals(Object o)
	{
		if(o instanceof Predicate)
		{
			Predicate p = (Predicate)o;
			if(name.equals(p.getName()))
			{
				ArrayList<String> sub = p.getSubjects();
				if(subjects.size() == sub.size())
				{
					for(int i=0; i < subjects.size(); i++)
					{
						if(!(subjects.get(i).equals(sub.get(i))))
							return false;
					}
					return true;
				}
				else
					return false;
			}
			else
				return false;
		}
		else
			return false;
	}

	Formula copy()
	{
		ArrayList<String> list = new ArrayList<String>();
		for(String s: subjects)
		{
			list.add(s);
		}
		return new Predicate(name, list);
	}

}

abstract class QuantFormula extends Formula
{
	String boundVar;
	Formula operand;

	public QuantFormula(String boundVar, Formula operand)
	{
		this.boundVar = boundVar;
		this.operand=operand;
	}

	public String getBoundVar()
	{
		return boundVar;
	}

	public Formula getOperand()
	{
		return operand;
	}

	public void assignValue(String var, String val)
	{
		operand.assignValue(var, val);
	}

	public Formula deleteQuant(String val)
	{
		operand.assignValue(boundVar, val);
		return operand;
	}
}

class AllFormula extends QuantFormula
{

	private ArrayList<String> assignedValues = new ArrayList<String>();

	public AllFormula(String boundVar, Formula operand)
	{
		super(boundVar, operand);
	}

	public String toString()
	{
		//return "all " + boundVar + operand;
		return "∀" + boundVar + getOperand();
	}

	public boolean equals(Object o)
	{
		if(o instanceof AllFormula)
		{
			AllFormula a = (AllFormula)o;
			Formula op = a.getOperand();
			return operand.equals(op);
		}
		return false;
	}		

	public ArrayList<String> getAssignedValues()
	{
		return assignedValues;
	}

	Formula copy()
	{
		return new AllFormula(boundVar, operand.copy());
	}
}

class ExistFormula extends QuantFormula
{

	public ExistFormula(String boundVar, Formula operand)
	{
		super(boundVar, operand);
	}

	public String toString()
	{
		//return "exist " + boundVar + operand;
		return "∃" + boundVar + getOperand();
	}

	public boolean equals(Object o)
	{
		if(o instanceof ExistFormula)
		{
			ExistFormula e = (ExistFormula)o;
			Formula op = e.getOperand();
			return operand.equals(op);
		}
		else
			return false;
	}

	Formula copy()
	{
		return new ExistFormula(boundVar, operand.copy());
	}
}

class NotFormula extends Formula
{
	private Formula operand;

	public NotFormula(Formula operand)
	{
		this.operand = operand;
	}

	public Formula getOperand()
	{
		return operand;
	}

	public String toString()
	{
		return "¬" + operand;
	}

	public boolean equals(Object o)
	{
		if(o instanceof NotFormula)
		{
			NotFormula n = (NotFormula)o;
			return operand.equals(n.getOperand());
		}
		else
			return false;
	}

	public void assignValue(String var, String val)
	{
		operand.assignValue(var, val);
	}

	Formula copy()
	{
		return new NotFormula(operand.copy());
	}
}

abstract class TwoOpFormula extends Formula
{
	protected Formula operand1;
	protected Formula operand2;

	public TwoOpFormula(Formula operand1, Formula operand2)
	{
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	public boolean equals(Object o)
	{
		if(this.getClass().equals(o.getClass()))
		{
			TwoOpFormula t = (TwoOpFormula)o;
			return (operand1.equals(t.getOperand1()) && operand2.equals(t.getOperand2()));
		}
		else
			return false;
	}

	public Formula getOperand1()
	{
		return operand1;
	}

	public Formula getOperand2()
	{
		return operand2;
	}

	public void assignValue(String var, String val)
	{
		operand1.assignValue(var, val);
		operand2.assignValue(var, val);
	}
	
}

class ImpFormula extends TwoOpFormula
{
	public ImpFormula(Formula operand1, Formula operand2)
	{
		super(operand1, operand2);
	}

	public String toString()
	{
		return "(" + operand1 + "→" + operand2 + ")";
	}

	Formula copy()
	{
		return new ImpFormula(operand1.copy(), operand2.copy());
	}
}

class IffFormula extends TwoOpFormula
{
	public IffFormula(Formula operand1, Formula operand2)
	{
		super(operand1, operand2);
	}

	public String toString()
	{
		return "(" + operand1 + " iff " + operand2 + ")";
	}

	Formula copy()
	{
		return new IffFormula(operand1.copy(), operand2.copy());
	}
}

class AndFormula extends TwoOpFormula
{
	public AndFormula(Formula operand1, Formula operand2)
	{
		super(operand1, operand2);
	}

	public AndFormula(String name1, String name2)
	{
		super(new Proposition(name1), new Proposition(name2));
	}

	public String toString()
	{
		return "(" + operand1 + "∧" + operand2 + ")";
	}

	Formula copy()
	{
		return new AndFormula(operand1.copy(), operand2.copy());
	}
}

class OrFormula extends TwoOpFormula
{
	public OrFormula(Formula operand1, Formula operand2)
	{
		super(operand1, operand2);
	}

	public OrFormula(String name1, String name2)
	{
		super(new Proposition(name1), new Proposition(name2));
	}

	public String toString()
	{
		return "(" + operand1 + "∨" + operand2 + ")";
	}

	Formula copy()
	{
		return new OrFormula(operand1.copy(), operand2.copy());
	}
}

class Tableau
{
	private TableauElement root;
	private ArrayList<String> valueList = new ArrayList<String>();
	//private int valueIndex = 0;
	private boolean dirty;
	private boolean allApplied = false;

	Tableau(ArrayList<Formula> list)
	{
		root = new TableauElement(null, list);
	}

	public void makeTableau()
	{
		root.checkValues(valueList);
		root.checkContradiction();
		doMake(root);
		if(valueList.size() == 0)
		{
			//allをはずすためにてきとうなvalueを入れてみる
			valueList.add("a");
			allApplied = true;
		}
		while(allApplied)
		{
			allApplied = false;
			doMake(root);
		}
	}

	private void doMake(TableauElement elem)
	{
		if(elem.isClosed())
			return;
		for(Formula f: elem.getList())
		{
			if(!f.isUsed())
			{
				if(f instanceof AndFormula)
				{
					applyAnd((AndFormula)f, elem);
					f.use();
					continue;
				}

				if(f instanceof NotFormula)
				{
					Formula g = ((NotFormula)f).getOperand();
					if(g instanceof NotFormula)
					{
						applyNotNot((NotFormula)f, elem);
						f.use();
						continue;
					}
					else if(g instanceof OrFormula)
					{
						applyNotOr((NotFormula)f, elem);
						f.use();
						continue;
					}
					else if(g instanceof ImpFormula)
					{
						applyNotImp((NotFormula)f, elem);
						f.use();
						continue;
					}
					else if(g instanceof ExistFormula)
					{
						applyNotExist((NotFormula)f, elem);
						f.use();
						continue;
					}
					else if(g instanceof AllFormula)
					{
						applyNotAll((NotFormula)f, elem);
						f.use();
						continue;
					}
					else if(g instanceof AndFormula)
					{
						applyNotAnd((NotFormula)f, elem);
						f.use();
						continue;
					}
					else if(g instanceof IffFormula)
					{
						applyNotIff((NotFormula)f, elem);
						f.use();
						continue;
					}
				}

				if(f instanceof OrFormula)
				{
					applyOr((OrFormula)f, elem);
					f.use();
					continue;
				}

				if(f instanceof ImpFormula)
				{
					applyImp((ImpFormula)f, elem);
					f.use();
					continue;
				}

				if(f instanceof IffFormula)
				{
					applyIff((IffFormula)f, elem);
					f.use();
					continue;
				}

				if(f instanceof ExistFormula)
				{
					applyExist((ExistFormula)f, elem);
					f.use();
					allApplied = true;
					continue;
				}

				if(f instanceof AllFormula)
				{
					AllFormula a = (AllFormula)f;
					ArrayList<String> assigned = a.getAssignedValues();
					for(String v: valueList)
					{
						boolean matched = false;
						for(String w: assigned)
						{
							if(w.equals(v))
							{
								matched = true;
								break;
							}
						}
						if(!matched)
						{
							applyAll(a, elem, v);
							assigned.add(v);
							allApplied = true;
							break;
						}
					}

				}
			}
		}

		TableauElement left = elem.getLeft();
		TableauElement right = elem.getRight();

		if(left != null)
		{
			doMake(left);
			if(right != null)
			{
				doMake(right);
			}
		}
	}

	/*
	private void doMakeAll(TableauElement elem)
	{
		if(elem.isClosed())
			return;
		for(Formula f: elem.getList())
		{
			if(!f.isUsed())
			{
				if(f instanceof AllFormula)
				{
					if(valueIndex >= valueList.size())
					{
						f.use();
						valueIndex = 0;
					}
					else
					{
						String s = valueList.get(valueIndex);
						applyAll((AllFormula)f, elem, s);
						valueIndex++;
					}
					continue;
				}
			}
		}
		TableauElement left = elem.getLeft();
		TableauElement right = elem.getRight();

		if(left != null)
		{
			doMakeAll(left);
			if(right != null)
			{
				doMakeAll(right);
			}
		}
	}
	*/

	private void applyAnd(AndFormula f, TableauElement elem)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		list.add(f.getOperand1().copy());
		list.add(f.getOperand2().copy());
		dirty = elem.addChild(list);
	}

	private void applyOr(OrFormula f, TableauElement elem)
	{
		ArrayList<Formula> list1 = new ArrayList<Formula>();
		ArrayList<Formula> list2 = new ArrayList<Formula>();
		list1.add(f.getOperand1().copy());
		list2.add(f.getOperand2().copy());
		dirty = elem.addChildren(list1, list2);
	}

	private void applyImp(ImpFormula f, TableauElement elem)
	{
		ArrayList<Formula> list1 = new ArrayList<Formula>();
		ArrayList<Formula> list2 = new ArrayList<Formula>();
		list1.add(new NotFormula(f.getOperand1().copy()));
		list2.add(f.getOperand2().copy());
		dirty = elem.addChildren(list1, list2);
	}

	private void applyIff(IffFormula f, TableauElement elem)
	{
		ArrayList<Formula> list1 = new ArrayList<Formula>();
		ArrayList<Formula> list2 = new ArrayList<Formula>();
		list1.add(f.getOperand1().copy());
		list1.add(f.getOperand2().copy());
		list2.add(new NotFormula(f.getOperand1().copy()));
		list2.add(new NotFormula(f.getOperand2().copy()));
		dirty = elem.addChildren(list1, list2);
	}

	private void applyNotNot(NotFormula f, TableauElement elem)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		Formula op = ((NotFormula)(f.getOperand())).getOperand();
		list.add(op.copy());
		dirty = elem.addChild(list);
	}

	private void applyNotAnd(NotFormula f, TableauElement elem)
	{
		ArrayList<Formula> list1 = new ArrayList<Formula>();
		ArrayList<Formula> list2 = new ArrayList<Formula>();
		Formula op1 = ((AndFormula)(f.getOperand())).getOperand1();
		Formula op2 = ((AndFormula)(f.getOperand())).getOperand2();
		list1.add(new NotFormula(op1.copy()));
		list2.add(new NotFormula(op2.copy()));
		dirty = elem.addChildren(list1, list2);
	}

	private void applyNotOr(NotFormula f, TableauElement elem)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		Formula op1 = ((OrFormula)(f.getOperand())).getOperand1();
		Formula op2 = ((OrFormula)(f.getOperand())).getOperand2();
		list.add(new NotFormula(op1.copy()));
		list.add(new NotFormula(op2.copy()));
		dirty = elem.addChild(list);
	}

	private void applyNotImp(NotFormula f, TableauElement elem)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		Formula op1 = ((ImpFormula)(f.getOperand())).getOperand1();
		Formula op2 = ((ImpFormula)(f.getOperand())).getOperand2();
		list.add(op1);
		list.add(new NotFormula(op2.copy()));
		dirty = elem.addChild(list);
	}

	private void applyNotIff(NotFormula f, TableauElement elem)
	{
		ArrayList<Formula> list1 = new ArrayList<Formula>();
		ArrayList<Formula> list2 = new ArrayList<Formula>();
		Formula op1 = ((IffFormula)(f.getOperand())).getOperand1();
		Formula op2 = ((IffFormula)(f.getOperand())).getOperand2();
		list1.add(op1.copy());
		list1.add(new NotFormula(op2.copy()));
		list2.add(new NotFormula(op1.copy()));
		list2.add(op2.copy());
		dirty = elem.addChildren(list1, list2);
	}

	private void applyNotAll(NotFormula f, TableauElement elem)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		Formula op = ((AllFormula)(f.getOperand())).getOperand();
		String bound = ((AllFormula)(f.getOperand())).getBoundVar();
		list.add(new ExistFormula(bound, new NotFormula(op.copy())));
		dirty = elem.addChild(list);
	}

	private void applyNotExist(NotFormula f, TableauElement elem)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		Formula op = ((ExistFormula)(f.getOperand())).getOperand();
		String bound = ((ExistFormula)(f.getOperand())).getBoundVar();
		list.add(new AllFormula(bound, new NotFormula(op.copy())));
		dirty = elem.addChild(list);
	}

	private void applyExist(ExistFormula f, TableauElement elem)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		String nv = getNewValue();
		valueList.add(nv);
		list.add(((QuantFormula)(f.copy())).deleteQuant(nv));
		dirty = elem.addChild(list);
	}

	private void applyAll(AllFormula f, TableauElement elem, String s)
	{
		ArrayList<Formula> list = new ArrayList<Formula>();
		AllFormula dup = (AllFormula)(f.copy());
		list.add(dup.deleteQuant(s));
		dirty = elem.addChild(list);
	}

	static private String[] valueTable = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
					      "v", "w", "x", "y", "z"};

	private String getNewValue()
	{
		for(String s: valueTable)
		{
			if(!containedValueCheck(s))
			{
				return s;
			}
		}

		for(int i=2; i < 10000; i++)
		{
			for(String s: valueTable)
			{
				String t = s + i;
				if(!containedValueCheck(t))
					return t;
			}
		}
		return "";
	}

	private boolean containedValueCheck(String s)
	{
		for(String t: valueList)
		{
			if(t.equals(s))
				return true;
		}
		return false;
	}
/*
	private void checkContradiction(TableauElement elem)
	{
		ArrayList<Formula> list = elem.getList();
		for(Formula f: list)
		{
			if(f instanceof NotFormula)
			{
				Formula g = ((NotFormula)f).getOperand();
				if(findSameFormula(elem, g))
				{
					elem.close();
					return;
				}
			}
			else
			{
				Formula g = new NotFormula(f);
				if(findSameFormula(elem,g))
				{
					elem.close();
					return;
				}
			}
		}
	}

	private boolean findSameFormula(TableauElement elem, Formula f)
	{
		ArrayList<Formula> list = elem.getList();
		for(Formula g: list)
		{
			if(g.equals(f))
				return true;
		}
		TableauElement parent = elem.getParent();
		if(parent != null)
			return findSameFormula(parent, f);
		else
			return false;
	}
*/
	public void print()
	{
		root.print(0);
	}

}

class TableauElement
{
	private ArrayList<Formula> list;
	//private ArrayList<String> values = new ArrayList<String>(); 
	private TableauElement parent = null;
	private TableauElement left = null;
	private TableauElement right = null;
	private boolean closed = false;
	
	TableauElement(TableauElement parent, ArrayList<Formula> list)
	{
		this.parent = parent;
		this.list = list;
	}

	public boolean isClosed()
	{
		return closed;
	}

	public void close()
	{
		closed=true;
	}

	public boolean allClosed()
	{
		if(closed)
			return true;
		else
		{
			if(left != null)
			{
				if(right != null)
				{
					boolean l = left.allClosed();
					boolean r = right.allClosed();
					return l && r;
				}
				else
					return left.allClosed();
			}
			else
				return false;
		}
	}

	public ArrayList<Formula> getList()
	{
		return list;
	}

	public TableauElement getParent()
	{
		return parent;
	}

	public TableauElement getLeft()
	{
		return left;
	}

	public TableauElement getRight()
	{
		return right;
	}

	private ArrayList<Formula> copyList(ArrayList<Formula> list)
	{
		ArrayList<Formula> dup = new ArrayList<Formula>();
		for(Formula f: list)
		{
			dup.add(f);
		}
		return dup;
	}

	public boolean addChild(ArrayList<Formula> list)
	{
		if(closed)
			return false;
		if(left == null)
		{
			left = new TableauElement(this, list);
			left.checkContradiction();
			return true;
		}
		else
		{
			if(right == null)
			{
				return left.addChild(list);
			}
			else
			{
				ArrayList<Formula> dup = copyList(list);
				boolean l = left.addChild(list);
				boolean r = right.addChild(dup);
				return l || r;
			}
		}
	}

	public boolean addChildren(ArrayList<Formula> list1, ArrayList<Formula> list2)
	{
		if(closed)
			return false;
		if(left == null)
		{
			left = new TableauElement(this, list1);
			left.checkContradiction();
			right = new TableauElement(this, list2);
			right.checkContradiction();
			return true;
		}
		else
		{
			if(right == null)
			{
				return left.addChildren(list1, list2);
			}
			else
			{
				ArrayList<Formula> dup1 = copyList(list1);
				ArrayList<Formula> dup2 = copyList(list2);
				boolean l = left.addChildren(list1, list2);
				boolean r = right.addChildren(dup1, dup2);
				return l || r;
			}
		}
	}

	public void checkContradiction()
	{
		ArrayList<Formula> list = getList();
		for(Formula f: list)
		{
			if(f instanceof NotFormula)
			{
				Formula g = ((NotFormula)f).getOperand();
				if(findSameFormula(this, g))
				{
					close();
					return;
				}
			}
			else
			{
				Formula g = new NotFormula(f);
				if(findSameFormula(this, g))
				{
					close();
					return;
				}
			}
		}
	}

	private boolean findSameFormula(TableauElement elem, Formula f)
	{
		ArrayList<Formula> list = elem.getList();
		for(Formula g: list)
		{
			if(g.equals(f))
				return true;
		}
		TableauElement parent = elem.getParent();
		if(parent != null)
			return findSameFormula(parent, f);
		else
			return false;
	}

	public void checkValues(ArrayList<String> valueList)
	{
		ArrayList<String> boundList = new ArrayList<String>();
		for(Formula f: list)
		{
			doCheckValues(f, boundList, valueList);
		}
	}

	private void doCheckValues(Formula f, ArrayList<String> boundList, ArrayList<String> valueList)
	{
		if(f instanceof Predicate)
		{
			Predicate p = (Predicate)f;
			ArrayList<String> sub = p.getSubjects();
			for(String s: sub)
			{
				if(!containedValue(boundList, s))
				{
					if(!containedValue(valueList, s))
					{
						valueList.add(s);
					}
				}
			}
		}
		else if(f instanceof TwoOpFormula)
		{
			TwoOpFormula t = (TwoOpFormula)f;
			doCheckValues(t.getOperand1(), boundList, valueList);
			doCheckValues(t.getOperand2(), boundList, valueList);
		}
		else if(f instanceof NotFormula)
		{
			NotFormula n = (NotFormula)f;
			doCheckValues(n.getOperand(), boundList, valueList);
		}
		else if(f instanceof AllFormula)
		{
			AllFormula a = (AllFormula)f;
			boundList.add(a.getBoundVar());
			doCheckValues(a.getOperand(), boundList, valueList);
			boundList.remove(boundList.size()-1);
		}
		else if(f instanceof ExistFormula)
		{
			ExistFormula e = (ExistFormula)f;
			boundList.add(e.getBoundVar());
			doCheckValues(e.getOperand(), boundList, valueList);
			boundList.remove(boundList.size()-1);
		}

	}

	private boolean containedValue(ArrayList<String> vlist, String s)
	{
		for(String t: vlist)
		{
			if(t.equals(s))
				return true;
		}
		return false;
	}

	/*
	public boolean containedValuePath(String s)
	{
		return containedValuePath(this, s);
	}

	public boolean containedValuePath(TableauElement elem, String s)
	{
		if(containedValue(elem.getValues(), s))
			return true;
		else
		{
			TableauElement p = elem.getParent();
			if(p != null)
				return containedValuePath(p, s);
			else
				return false;
		}
	}
	*/

	public void print(int tabCount)
	{
		for(int i=0; i < tabCount; i++)
		{
			System.out.print("\t");
		}
		for(int i=0; i < list.size(); i++)
		{
			Formula f = list.get(i);
			String str;
			if(f instanceof TwoOpFormula)
			{
				String s = f.toString();
				str = s.substring(1, s.length()-1);
			}
			else
				str = f.toString();
			if(i == list.size() - 1)
				System.out.print(str);
			else
				System.out.print(str + ",");
		}
		if(closed)
			System.out.print("...contradiction!");
		System.out.println();
		if(left != null)
		{
			left.print(tabCount+1);
			if(right != null)
				right.print(tabCount+1);
		}
	}
}
