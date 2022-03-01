<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                exclude-result-prefixes="fo">

  <xsl:template match="div">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="my-page" page-height="297mm" page-width="210mm" font-family="DejaVuSans">
          <fo:region-body margin="1in" margin-top="3cm" margin-left="2cm"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="my-page">

        <fo:flow flow-name="xsl-region-body">
          <fo:block-container background-color="#5FBFE1" height="2.5cm" font-family="DejaVuSans">
            <fo:block>Flow</fo:block>
          </fo:block-container>
        </fo:flow>

      </fo:page-sequence>
    </fo:root>

  </xsl:template>

</xsl:stylesheet>