%options package=org.eclipse.imp.formatting.parser
%options template=dtParserTemplate.gi
%options import_terminals=FormattingSpecificationLexer.gi
%options parent_saved,automatic_ast=toplevel,visitor=preorder,ast_directory=./Ast,ast_type=ASTNode
--
-- This is just a sample grammar and not a real grammar for FormattingSpecification
--

%Globals
    /.import org.eclipse.imp.parser.IParser;
    ./
%End



%Define
    $ast_class /.Object./
    $additional_interfaces /., IParser./
%End

%Terminals
    --            
    -- Here, you may list terminals needed by this grammar.
    -- Furthermore, a terminal may be mapped into an alias
    -- that can also be used in a grammar rule. In addition,
    -- when an alias is specified here it instructs the
    -- generated parser to use the alias in question when
    -- referring to the symbol to which it is aliased. For
    -- example, consider the following definitions:
    --
    begin_formatter 
    end_formatter   
    begin_language 
    end_language
    begin_rules
    end_rules
    begin_rule
    end_rule
    begin_box
    end_box
    begin_example
    end_example
         
         IDENTIFIER 
         any
         
%End

%Start
    specification
%End

%Recover
%End

%Rules
    specification ::= begin_formatter language rules example end_formatter
    language ::= begin_language IDENTIFIER end_language
    rules ::= begin_rules rulelist end_rules
    rulelist$$rule ::= $empty
              | rulelist rule
    rule ::= begin_rule box end_rule
    box  ::= begin_box any end_box
    example ::= begin_example any end_example
%End

$Headers
%End
