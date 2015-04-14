# -*- coding: utf-8 -*-
# downloads flag images from crowdin

import requests
from xml.dom import minidom

def getSupportedLanguages():
	r = requests.get('https://api.crowdin.com/api/supported-languages')
	xmldoc = minidom.parseString(r.text)
	langxml = xmldoc.getElementsByTagName('language')

	langs = []
	for lang in langxml:
		langs.append(lang.getElementsByTagName('crowdin_code')[0].firstChild.nodeValue)
	return langs

langs = getSupportedLanguages()
for lang in langs:
	r = requests.get('https://d1ztvzf22lmr1j.cloudfront.net/images/flags/' + lang + '.png', stream=True)
	if r.status_code == 200:
		with open('../src/main/resources/gui/flags/' + lang + '.png', 'wb') as f:
			for chunk in r.iter_content():
				f.write(chunk)
