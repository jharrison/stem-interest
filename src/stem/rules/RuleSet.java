package stem.rules;

import java.util.ArrayList;

import sim.util.Interval;

public class RuleSet
{
	public ArrayList<Rule> rules = new ArrayList<Rule>();
	
	FriendRule friendRule = new FriendRule();
	ParentRule parentRule = new ParentRule();
	UnrelatedAdultRule unrelatedAdultRule = new UnrelatedAdultRule();
	ChoiceRule choiceRule = new ChoiceRule();
	MakeFriendRule makeFriendRule = new MakeFriendRule();
	
	public RuleSet() {
		rules.add(friendRule);
		rules.add(parentRule);
		rules.add(unrelatedAdultRule);
		rules.add(choiceRule);
		rules.add(makeFriendRule);
	}
	
	public boolean getFriendRuleEnabled() { return friendRule.isActive; }
	public void setFriendRuleEnabled(boolean val) { friendRule.isActive = val; }	
	public double getFriendRuleWeight() { return friendRule.weight; }
	public void setFriendRuleWeight(double val) { friendRule.weight = val; }
	public Object domFriendRuleWeight() { return new Interval(0.0, 5.0); }
	
	public boolean getParentRuleEnabled() { return parentRule.isActive; }
	public void setParentRuleEnabled(boolean val) { parentRule.isActive = val; }	
	public double getParentRuleWeight() { return parentRule.weight; }
	public void setParentRuleWeight(double val) { parentRule.weight = val; }
	public Object domParentRuleWeight() { return new Interval(0.0, 5.0); }
	
	public boolean getUnrelatedAdultRuleEnabled() { return unrelatedAdultRule.isActive; }
	public void setUnrelatedAdultRuleEnabled(boolean val) { unrelatedAdultRule.isActive = val; }	
	public double getUnrelatedAdultRuleWeight() { return unrelatedAdultRule.weight; }
	public void setUnrelatedAdultRuleWeight(double val) { unrelatedAdultRule.weight = val; }
	public Object domUnrelatedAdultRuleWeight() { return new Interval(0.0, 5.0); }
	
	public boolean getChoiceRuleEnabled() { return choiceRule.isActive; }
	public void setChoiceRuleEnabled(boolean val) { choiceRule.isActive = val; }	
	public double getChoiceRuleWeight() { return choiceRule.weight; }
	public void setChoiceRuleWeight(double val) { choiceRule.weight = val; }
	public Object domChoiceRuleWeight() { return new Interval(0.0, 5.0); }
	

	public boolean getMakeFriendRuleEnabled() { return makeFriendRule.isActive; }

}
