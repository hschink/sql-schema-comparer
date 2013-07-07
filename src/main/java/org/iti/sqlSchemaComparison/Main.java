package org.iti.sqlSchemaComparison;

import static org.kohsuke.args4j.ExampleMode.ALL;

import java.util.List;

import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.frontends.SqlStatementFrontend;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontend;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Main {

	private static class CommandLineOption {
		
		@Option(name="-statement", 
				usage="Statement to validate against a schema (at least one database must be passed)", 
				required=false)
		private String statement;
				
		@Argument(usage="One ore more SQLite files - If a statement and more than one SQLite file are given, the first" + 
				"SQLite file is treated as the statement's original (working) database schema.",
			  required=false,
			  multiValued=true)
		private List<String> databases;
	}
	
	public static void main(String[] args) {
		CommandLineOption option = new CommandLineOption();
		CmdLineParser parser = new CmdLineParser(option);
		
		try {
			parser.parseArgument(args);
			
			if (option.statement == null && option.databases == null)
				throw new CmdLineException(parser, "No arguments passed!");
			
			if ((option.statement != null && option.statement != "")
					&& (option.databases == null || option.databases.size() == 0))
				throw new CmdLineException(parser, "Statement Validation: No database passed!");
			
			if ((option.statement == null || option.statement == "")
					&& (option.databases == null || option.databases.size() <= 1))
				throw new CmdLineException(parser, "Schema Comparison: Not enough databases passed!");
			
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println();
            System.err.println("java SampleMain [options...] arguments...\n");
            parser.printUsage(System.err);
            System.err.println();
            System.err.println(" Example: java SampleMain" + parser.printExample(ALL));

            return;
		}
		
		if (option.statement == null || option.statement == "") {
			compareDatabaseSchemas(option.databases);
		} else {
			compareDatabaseStatement(option.statement, option.databases);
		}
	}

	private static void compareDatabaseStatement(String statement,
			List<String> databases) {
		Graph<ISqlElement, DefaultEdge> baseSchema = getBaseSchema(databases);
		ISqlSchemaFrontend statementFrontend = new SqlStatementFrontend(statement, baseSchema);
		Graph<ISqlElement, DefaultEdge> statementSchema = statementFrontend.createSqlSchema();
		
		compareDatabaseStatement(statement, statementSchema, databases);
	}

	private static Graph<ISqlElement, DefaultEdge> getBaseSchema(
			List<String> databases) {
		Graph<ISqlElement, DefaultEdge> baseSchema = null;
		
		if (databases.size() > 1) {
			String baseDatabaseFilePath = databases.remove(0);
			
			ISqlSchemaFrontend baseFrontend = new SqliteSchemaFrontend(baseDatabaseFilePath);
			
			baseSchema = baseFrontend.createSqlSchema();
		}
		
		return baseSchema;
	}

	private static void compareDatabaseStatement(
			String statement,
			Graph<ISqlElement, DefaultEdge> statementSchema,
			List<String> databases) {
		if (databases.size() > 0) {
			String databaseFilePath = databases.remove(0);
			
			ISqlSchemaFrontend frontend = new SqliteSchemaFrontend(databaseFilePath);
			
			Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
			
			SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(schema);
			SqlStatementExpectationValidationResult result = validator.computeGraphMatching(statementSchema);

			System.out.println("> " + statement + "\n");
			System.out.println(result.toString());
			
			compareDatabaseStatement(statement, statementSchema, databases);
		}
	}

	private static void compareDatabaseSchemas(List<String> databases) {
		if (databases.size() > 1) {
			String baseDatabaseFilePath = databases.remove(0);
			String nextDatabaseFilePath = databases.get(0);
			
			ISqlSchemaFrontend frontend1 = new SqliteSchemaFrontend(baseDatabaseFilePath);
			ISqlSchemaFrontend frontend2 = new SqliteSchemaFrontend(nextDatabaseFilePath);
			
			Graph<ISqlElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
			Graph<ISqlElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
			
			SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
			
			System.out.println(String.format("[%s] <=> [%s]", baseDatabaseFilePath, nextDatabaseFilePath));
			System.out.println();
			
			if (comparer.isIsomorphic())
				System.out.println(String.format("Schemas [%s] and [%s] are isomorphic!", baseDatabaseFilePath, nextDatabaseFilePath));
			else
				System.out.println(comparer.comparisonResult.toString());
			
			System.out.println();
			System.out.println(new String(new char[80]).replace('\0', '='));
			System.out.println();
			
			compareDatabaseSchemas(databases);
		}
	}
	
}
