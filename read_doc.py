import zipfile
import xml.etree.ElementTree as ET
import sys

sys.stdout.reconfigure(encoding='utf-8')

def extract_text():
    with zipfile.ZipFile('CNPM - Nhóm 14 (3).docx') as doc:
        tree = ET.XML(doc.read('word/document.xml'))
        PARA = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}p'
        TEXT = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}t'
        with open('report.txt', 'w', encoding='utf-8') as f:
            for p in tree.iter(PARA):
                texts = [node.text for node in p.iter(TEXT) if node.text]
                if texts:
                    f.write(''.join(texts) + '\n')

extract_text()
