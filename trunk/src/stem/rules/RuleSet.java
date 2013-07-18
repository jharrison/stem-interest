package stem.rules;

import java.util.ArrayList;

import sim.util.Interval;

public class RuleSet
{
	public ArrayList<Rule> rules = new ArrayList<Rule>();
	
	FriendRule friendRule = new FriendRule();
	ParentRule parentRule = new ParentRule();
	UnrelatedAdultRule unrelatedAdultRule = new UnrelatedAdultRule();
	UnrelatedAdultRuleV2 unrelatedAdultRuleV2 = new UnrelatedAdultRuleV2();
	ChoiceRule choiceRule = new ChoiceRule();
	ChoiceRuleV2 choiceRuleV2 = new ChoiceRuleV2();
	MakeFriendRule makeFriendRule = new MakeFriendRule();
	
	public RuleSet() {
		rules.add(friendRule);
		rules.add(parentRule);
		rules.add(unrelatedAdultRule);
		rules.add(unrelatedAdultRuleV2);
		rules.add(choiceRule);
		rules.add(choiceRuleV2);
		rules.add(makeFriendRule);
		
		unrelatedAdultRule.isActive = false;
		choiceRule.isActive = false;
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
	
	public boolean getUnrelatedAdultRuleV2Enabled() { return unrelatedAdultRuleV2.isActive; }
	public void setUnrelatedAdultRuleV2Enabled(boolean val) { unrelatedAdultRuleV2.isActive = val; }	
	public double getUnrelatedAdultRuleV2Weight() { return unrelatedAdultRuleV2.weight; }
	public void setUnrelatedAdultRuleV2Weight(double val) { unrelatedAdultRuleV2.weight = val; }
	public Object domUnrelatedAdultRuleV2Weight() { return new Interval(0.0, 5.0); }
	
	public boolean getChoiceRuleEnabled() { return choiceRule.isActive; }
	public void setChoiceRuleEnabled(boolean val) { choiceRule.isActive = val; }	
	public double getChoiceRuleWeight() { return choiceRule.weight; }
	public void setChoiceRuleWeight(double val) { choiceRule.weight = val; }
	public Object domChoiceRuleWeight() { return new Interval(0.0, 5.0); }
	
	public boolean getChoiceRuleV2Enabled() { return choiceRuleV2.isActive; }
	public void setChoiceRuleV2Enabled(boolean val) { choiceRuleV2.isActive = val; }	
	public double getChoiceRuleV2Weight() { return choiceRuleV2.weight; }
	public void setChoiceRuleV2Weight(double val) { choiceRuleV2.weight = val; }
	public Object domChoiceRuleV2Weight() { return new Interval(0.0, 5.0); }
	
	public boolean getMakeFriendRuleEnabled() { return makeFriendRule.isActive; }
	public void setMakeFriendRuleEnabled(boolean val) { makeFriendRule.isActive = val; }	

}
