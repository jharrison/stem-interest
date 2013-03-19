package stem.rules;

import java.util.ArrayList;

public class RuleSet
{
	public ArrayList<Rule> rules = new ArrayList<Rule>();
	
	FriendRule friendRule = new FriendRule();
	ParentRule parentRule = new ParentRule();
	UnrelatedAdultRule unrelatedAdultRule = new UnrelatedAdultRule();
	ChoiceRule choiceRule = new ChoiceRule();
	
	public RuleSet() {
		rules.add(friendRule);
		rules.add(parentRule);
		rules.add(unrelatedAdultRule);
		rules.add(choiceRule);
	}
	
	public boolean getFriendRuleEnabled() { return friendRule.isActive; }
	public void setFriendRuleEnabled(boolean val) { friendRule.isActive = val; }	
	public double getFriendRuleWeight() { return friendRule.weight; }
	public void setFriendRuleWeight(double val) { friendRule.weight = val; }
	
	public boolean getParentRuleEnabled() { return parentRule.isActive; }
	public void setParentRuleEnabled(boolean val) { parentRule.isActive = val; }	
	public double getParentRuleWeight() { return parentRule.weight; }
	public void setParentRuleWeight(double val) { parentRule.weight = val; }
	
	public boolean getUnrelatedAdultRuleEnabled() { return unrelatedAdultRule.isActive; }
	public void setUnrelatedAdultRuleEnabled(boolean val) { unrelatedAdultRule.isActive = val; }	
	public double getUnrelatedAdultRuleWeight() { return unrelatedAdultRule.weight; }
	public void setUnrelatedAdultRuleWeight(double val) { unrelatedAdultRule.weight = val; }
	
	public boolean getChoiceRuleEnabled() { return choiceRule.isActive; }
	public void setChoiceRuleEnabled(boolean val) { choiceRule.isActive = val; }	
	public double getChoiceRuleWeight() { return choiceRule.weight; }
	public void setChoiceRuleWeight(double val) { choiceRule.weight = val; }

}
