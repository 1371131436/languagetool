# -*- coding: iso-8859-1 -*-
# Rule that checks the use of 'a' vs. 'an'
# (c) 2003 Daniel Naber <daniel.naber@t-online.de>
#
#$rcs = ' $Id: huAvsAnRule.py,v 1.1 2004-05-23 21:47:13 dnaber Exp $ ' ;
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or (at
# your option) any later version.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
# USA

import os
import sys

sys.path.append("..")
import Rules

class huAvsAnRule(Rules.Rule):
	"""Check if the determiner (if any) before a word is:
	-'an' if the next word starts with a vowel
	-'a' if the next word does not start with a vowel
	This rule knows about some exceptions (e.g. 'an hour')."""

	#requires_a_file = os.path.join("data", "det_a.txt")
	#requires_an_file = os.path.join("data", "det_an.txt")

	def __init__(self):
		Rules.Rule.__init__(self, "DET", "Use of 'a' vs. use of 'an'.", 0, None)
		#self.requires_a = self.loadWords(self.requires_a_file)
		#self.requires_an = self.loadWords(self.requires_an_file)
		return

#	def loadWords(self, filename):
#		f = open(filename)
#		l = []
#		while 1:
#			line = f.readline()
#			if not line:
#				break
#			if line.startswith("#") or line.strip() == '':
#				continue
#			l.append(line.strip().lower())
#		f.close()
#		return l

	def match(self, tagged_words, chunks, position_fix=0):
		matches = []
		text_length = 0
		i = 0
		#print tagged_words
		while 1:
			if i >= len(tagged_words)-2:
				break
			org_word = tagged_words[i][0]
			org_word_next = tagged_words[i+2][0]
			#print "<tt>'%s' -- '%s'</tt><br>" % (org_word, org_word_next)
			if org_word.lower() == 'a':
				err = 0
				if org_word_next.lower() in self.requires_an:
					err = 1
				elif len(org_word_next) > 0 and org_word_next[0].lower() in ('a', 'e', 'i', 'o', 'u','�','�','�','�','�','�','�') and \
					not org_word_next.lower() in self.requires_a:
					err = 1
				if err:
					matches.append(Rules.RuleMatch(self.rule_id,
						text_length+position_fix, text_length+len(org_word)+position_fix,
						"Use <em>an</em> instead of <em>a</em> if the following "+
						"word starts with a vowel sound, e.g. 'an article', "+
						"'an hour'", org_word))
			elif org_word.lower() == 'an':
				err = 0
				if org_word_next.lower() in self.requires_a:
					err = 1
				elif len(org_word_next) > 0 and \
					(not org_word_next[0].lower() in ('a', 'e', 'i', 'o', 'u')) and \
					not org_word_next.lower() in self.requires_an:
					err = 1
				if err:
					matches.append(Rules.RuleMatch(self.rule_id,
						text_length++position_fix, text_length+len(org_word)+position_fix,
						"Use <em>a</em> instead of <em>an</em> if the following "+
						"word doesn't start with a vowel sound, e.g. 'a test', "+
						"'a university'", org_word))
				pass
			text_length = text_length + len(org_word)
			i = i + 1
		return matches
