<#-- Minimal stand-ins for the front office macros of lutece-core, used by DocumentTemplatesRenderingTest which renders without Lutece -->
<#macro cTpl tpl=''><#nested></#macro>
<#macro cBlock type='div' class='' id='' params=''><${type}<#if class!=''> class="${class}"</#if><#if id!=''> id="${id}"</#if>><#nested></${type}></#macro>
<#macro cText type='p' id='' class='' params=''><p<#if class!=''> class="${class}"</#if>><#nested></p></#macro>
<#macro cInline type='span' class='' id='' params=''><${type}<#if class!=''> class="${class}"</#if>><#nested></${type}></#macro>
