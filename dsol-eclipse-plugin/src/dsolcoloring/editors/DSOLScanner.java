package dsolcoloring.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

public class DSOLScanner extends RuleBasedScanner {
	public DSOLScanner() {
		
		IToken defaultToken = new Token(new TextAttribute(DSOLEditor.DEFAULT));

		setDefaultReturnToken(defaultToken);

		WordRule rule = new WordRule(new IWordDetector() {
			public boolean isWordStart(char c) {
				if (Character.isWhitespace(c)) {
					return false;
				}
				return Character.isLowerCase(c);
			}

			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
		}, defaultToken);

		Token keyword = new Token(new TextAttribute(DSOLEditor.KEYWORD, null,
				SWT.BOLD));
		String[] KEYWORDS = new String[] { "action", "pre", "post", "seam" };
		// add tokens for each reserved word
		for (int n = 0; n < KEYWORDS.length; n++) {
			rule.addWord(KEYWORDS[n], keyword);
		}

		WordRule ruleParam = new WordRule(new IWordDetector() {
			public boolean isWordStart(char c) {
				return Character.isUpperCase(c);
			}

			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
		}, new Token(new TextAttribute(DSOLEditor.PARAM)));

		setRules(new IRule[] {
				new EndOfLineRule("//", new Token(new TextAttribute(
						DSOLEditor.COMMENT))),
				rule,
				ruleParam,
				new MultiLineRule("/*", "*/", new Token(new TextAttribute(
						DSOLEditor.COMMENT)), (char) 0, true),
				new WhitespaceRule(new IWhitespaceDetector() {
					public boolean isWhitespace(char c) {
						return Character.isWhitespace(c);
					}
				}), });
	}
}