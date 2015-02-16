package stem.rules;

import java.util.ArrayList;

import sim.util.Interval;

public class RuleSet
{
	public ArrayList<Rule> rules = new ArrayList<Rule>();
	
	FriendRule friendRule = new FriendRule();
	ParentRule parentRule = new ParentRule();
	LeaderRule leaderRule = new LeaderRule();
	LeaderRuleV2 leaderRuleV2 = new LeaderRuleV2();
	ChoiceRule choiceRule = new ChoiceRule();
	ChoiceRuleV2 choiceRuleV2 = new ChoiceRuleV2();
	MakeFriendRule makeFriendRule = new MakeFriendRule();
	public EncouragementRule encouragementRule = new EncouragementRule();
	
	public RuleSet() {
		rules.add(friendRule);
		rules.add(parentRule);
		rules.add(leaderRule);
		rules.add(leaderRuleV2);
		rules.add(choiceRule);
		rules.add(choiceRuleV2);
//		rules.add(makeFriendRule);
		rules.add(encouragementRule);
		
		leaderRule.isActive = false;
		choiceRule.isActive = false;
		makeFriendRule.isActive = false;
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
	
	public boolean getLeaderRuleEnabled() { return leaderRule.isActive; }
	public void setLeaderRuleEnabled(boolean val) { leaderRule.isActive = val; }	
	public double getLeaderRuleWeight() { return leaderRule.weight; }
	public void setLeaderRuleWeight(double val) { leaderRule.weight = val; }
	public Object domLeaderRuleWeight() { return new Interval(0.0, 5.0); }
	
	public boolean getLeaderRuleV2Enabled() { return leaderRuleV2.isActive; }
	public void setLeaderRuleV2Enabled(boolean val) { leaderRuleV2.isActive = val; }	
	public double getLeaderRuleV2Weight() { return leaderRuleV2.weight; }
	public void setLeaderRuleV2Weight(double val) { leaderRuleV2.weight = val; }
	public Object domLeaderRuleV2Weight() { return new Interval(0.0, 5.0); }
	
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
	
//	public boolean getMakeFriendRuleEnabled() { return makeFriendRule.isActive; }
//	public void setMakeFriendRuleEnabled(boolean val) { makeFriendRule.isActive = val; }	
	
	public boolean getEncouragementRuleEnabled() { return encouragementRule.isActive; }
	public void setEncouragementRuleEnabled(boolean val) { encouragementRule.isActive = val; }	

}
