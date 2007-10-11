--
-- The Java KeyWord Lexer
--
%options package=org.eclipse.imp.formatting.parser
%options template=KeywordTemplate.gi
%options la=12

%Include
    KWLexerLowerCaseMap.gi
%End

%Export

    -- List all the keywords the kwlexer will export to the lexer and parser
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
%End

%Terminals
    a    b    c    d    e    f    g    h    i    j    k    l    m
    n    o    p    q    r    s    t    u    v    w    x    y    z
    GreaterThan ::= '<'
    LessThan    ::= '>'
    Slash       ::= '/'
%End

%Start
    Keyword
%End

%Rules

    -- The Goal for the parser is a single Keyword

    Keyword ::= < f o r m a t t e r >
        /.$BeginAction
            $setResult($_begin_formatter);
          $EndAction
        ./

    Keyword ::= < / f o r m a t t e r >
        /.$BeginAction
            $setResult($_end_formatter);
          $EndAction
        ./
        
     Keyword ::= < l a n g u a g e >
        /.$BeginAction
            $setResult($_begin_language);
          $EndAction
        ./

    Keyword ::= < / l a n g u a g e >
        /.$BeginAction
            $setResult($_end_language);
          $EndAction
        ./
        
    Keyword ::= < r u l e s >
        /.$BeginAction
            $setResult($_begin_rules);
          $EndAction
        ./

    Keyword ::= < / r u l e s >
        /.$BeginAction
            $setResult($_end_rules);
          $EndAction
        ./
        
    Keyword ::= < r u l e  >
        /.$BeginAction
            $setResult($_begin_rule);
          $EndAction
        ./

    Keyword ::= < / r u l e  >
        /.$BeginAction
            $setResult($_end_rule);
          $EndAction
        ./
        
    Keyword ::= < b o x  >
        /.$BeginAction
            $setResult($_begin_box);
          $EndAction
        ./

    Keyword ::= < / b o x >
        /.$BeginAction
            $setResult($_end_box);
          $EndAction
        ./
        
    Keyword ::= < e x a m p l e  >
        /.$BeginAction
            $setResult($_begin_box);
          $EndAction
        ./

    Keyword ::= < / e x a m p l e >
        /.$BeginAction
            $setResult($_end_box);
          $EndAction
        ./
%End
