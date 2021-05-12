package com.lemon.app.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统代码库
 * </p>
 *
 * @author hai-zhang
 * @since 2021-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_coding")
public class SysCoding implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id_", type = IdType.FreeMarker template error (DEBUG mode; use RETHROW in production!):
The following has evaluated to null or missing:
==> idType  [in template "entity.java.ftl" at line 63, column 61]

----
Tip: If the failing expression is known to legally refer to something that's sometimes null or missing, either specify a default value like myOptionalVar!myDefault, or use <#if myOptionalVar??>when-present<#else>when-missing</#if>. (These only cover the last step of the expression; to cover the whole expression, use parenthesis: (myOptionalVar.foo)!myDefault, (myOptionalVar.foo)??
----

----
FTL stack trace ("~" means nesting-related):
	- Failed at: ${idType}  [in template "entity.java.ftl" at line 63, column 59]
----

Java stack trace (for programmers):
----
freemarker.core.InvalidReferenceException: [... Exception message was already printed; see it above ...]
	at freemarker.core.InvalidReferenceException.getInstance(InvalidReferenceException.java:134)
	at freemarker.core.EvalUtil.coerceModelToTextualCommon(EvalUtil.java:481)
	at freemarker.core.EvalUtil.coerceModelToStringOrMarkup(EvalUtil.java:401)
	at freemarker.core.EvalUtil.coerceModelToStringOrMarkup(EvalUtil.java:370)
	at freemarker.core.DollarVariable.calculateInterpolatedStringOrMarkup(DollarVariable.java:100)
	at freemarker.core.DollarVariable.accept(DollarVariable.java:63)
	at freemarker.core.Environment.visit(Environment.java:347)
	at freemarker.core.Environment.visit(Environment.java:353)
	at freemarker.core.Environment.visit(Environment.java:353)
	at freemarker.core.Environment.visit(Environment.java:389)
	at freemarker.core.IteratorBlock$IterationContext.executedNestedContentForCollOrSeqListing(IteratorBlock.java:321)
	at freemarker.core.IteratorBlock$IterationContext.executeNestedContent(IteratorBlock.java:271)
	at freemarker.core.IteratorBlock$IterationContext.accept(IteratorBlock.java:244)
	at freemarker.core.Environment.visitIteratorBlock(Environment.java:657)
	at freemarker.core.IteratorBlock.acceptWithResult(IteratorBlock.java:108)
	at freemarker.core.IteratorBlock.accept(IteratorBlock.java:94)
	at freemarker.core.Environment.visit(Environment.java:347)
	at freemarker.core.Environment.visit(Environment.java:353)
	at freemarker.core.Environment.process(Environment.java:326)
	at freemarker.template.Template.process(Template.java:383)
	at com.lemon.schemaql.util.generator.ModuleCodeGenerator.writeSourceFile(ModuleCodeGenerator.java:282)
	at com.lemon.schemaql.util.generator.ModuleCodeGenerator.fillEntityData(ModuleCodeGenerator.java:212)
	at com.lemon.schemaql.util.generator.ModuleCodeGenerator.lambda$generateEntity$1(ModuleCodeGenerator.java:122)
	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
	at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175)
	at java.util.HashMap$KeySpliterator.forEachRemaining(HashMap.java:1556)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
	at com.lemon.schemaql.util.generator.ModuleCodeGenerator.generateEntity(ModuleCodeGenerator.java:122)
	at com.lemon.schemaql.util.generator.ModuleCodeGenerator.generate(ModuleCodeGenerator.java:88)
	at com.lemon.schemaql.util.generator.ModuleCodeGenerator.generate(ModuleCodeGenerator.java:62)
	at com.lemon.schemaql.util.SchemaCodeGenerator.main(SchemaCodeGenerator.java:43)
