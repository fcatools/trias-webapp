package de.unikassel.cs.kde.trias;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import de.unikassel.cs.kde.trias.io.ModelReaderWriter;
import de.unikassel.cs.kde.trias.model.Context;
import de.unikassel.cs.kde.trias.model.TriConcept;


/**
 * 
 * @author:  rja
 * @version: $Id: TriasWeb.java,v 1.2 2008-07-17 14:23:50 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasWeb {

	private static final int MAX_CONTEXT_SIZE = 1000;
	private static Logger log = Logger.getLogger(TriasWeb.class);

	public TriConcept<String>[] trias(final Context<String> context, 
			final int minSuppObjects, 
			final int minSuppAttributes,
			final int minSuppConditions) {
		
		log.info("Got context with " + context.getRelation().length + " triples.");
		log.info("Context:\n" + context);
		
		final ModelReaderWriter<String> modelReaderWriter = new ModelReaderWriter<String>(context);
		
		/*
		 * check size restriction
		 */
		if (context.getRelation().length > MAX_CONTEXT_SIZE) {
			final String message = "Given context too large (> " + MAX_CONTEXT_SIZE + " triples).";
			log.error(message);
			throw new RuntimeException(message);
		}
		
		
		/*
		 * set trias up
		 */
		final Trias trias = new Trias();
		log.info("Setting minimal support to (" + minSuppObjects + ", " + minSuppAttributes + ", " + minSuppConditions + ").");
		trias.setMinSupportPerDimension(new int[]{minSuppObjects, minSuppAttributes, minSuppConditions});
		trias.setTriConceptWriter(modelReaderWriter);
		
		try {
			final int[][] itemlist = modelReaderWriter.getItemlist();
			log.info("Got itemList from reader: ");
			for (final int[] item: itemlist) {
				log.info(Arrays.toString(item));
			}
			trias.setItemList(itemlist);
		} catch (NumberFormatException e) {
			log.fatal(e);
		}
		final int[] numberOfItemsPerDimension = modelReaderWriter.getNumberOfItemsPerDimension();
		log.info("Setting number of items per dimension to " + Arrays.toString(numberOfItemsPerDimension) + ".");
		trias.setNumberOfItemsPerDimension(numberOfItemsPerDimension);

		
		/*
		 * run trias
		 */
		try {
			log.info("Starting Trias.");
			trias.doWork();
			log.info("Finished Trias.");
		} catch (IOException e) {
			log.fatal(e);		}
		/*
		 * extract tri lattice
		 */
		final TriConcept<String>[] triLattice = modelReaderWriter.getTriLattice();
		log.info("Returning result tri lattice containing " + triLattice.length + " tri concepts:");
		log.info(Arrays.toString(triLattice));
		return triLattice;
	}

}

