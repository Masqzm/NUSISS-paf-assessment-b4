package vttp2023.batch4.paf.assessment.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 * 
	 * 	db.listings.aggregate([
	 * 		{
				$match: {
					'address.country': {
						$regex: 'Australia',
						$options: 'i'
					},
					'address.suburb': { $ne: null, $ne: "" }
				}
			},
			{
				$group: { _id: "$address.suburb" }
			},
			{
				$sort:  { _id: 1 }
			}
		])
	 */
	public List<String> getSuburbs(String country) {
        // Match operation (filter by country)
        Criteria criteriaCountry = Criteria.where("address.country")
                            	.regex(country, "i");    
		// Match operation (filter null/empty address.suburb values)
		Criteria criteriaSuburb = Criteria.where("address.suburb")
								.ne(null)						
								.ne("");


		Criteria criteria = criteriaCountry.andOperator(criteriaSuburb);
        
		MatchOperation filterByCountryValidSuburb = Aggregation.match(criteria);
		GroupOperation groupSuburb = Aggregation.group("address.suburb");
		SortOperation sortSuburbAsc = Aggregation.sort(Sort.Direction.ASC, "_id");

		Aggregation pipeline = Aggregation.newAggregation(filterByCountryValidSuburb, groupSuburb, sortSuburbAsc);
		
		List<Document> results = template.aggregate(pipeline, "listings", Document.class).getMappedResults();
		
		List<String> suburbs = results.stream()
							.map(doc -> doc.getString("_id"))
							.collect(Collectors.toList());
		
		return suburbs;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 *	db.listings.aggregate([
			{
				$match: {
					'address.suburb': { 
						$regex: suburb,
						$options: 'i'
					},
					accommodates: { $gte: persons },
					min_nights: { $lte: duration },
					price: { $lte: priceRange }
					//max_nights: { $gte: duration },
					//price: { $gte: 50, $lte: priceRange }
				}
			},
			{
				$project: { 
					_id: 1, 
					name: 1, 
					accommodates: 1, 
					price: 1
				} 
			},
			{
				// Sort desc
				$sort:  { price: -1 }
			}
		])
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		// Match operations
        Criteria criteriaSuburb = Criteria.where("address.suburb")
                            	.regex(suburb, "i");
        Criteria criteriaAccomm = Criteria.where("accommodates").gte(persons);
        Criteria criteriaMinNights = Criteria.where("min_nights").lte(duration);
		Criteria criteriaPriceRange = Criteria.where("price").lte(priceRange);
        //Criteria criteriaMaxNights = Criteria.where("max_nights").gte(duration);
        //Criteria criteriaPriceRange = Criteria.where("price").gte(50).lte(priceRange);

		MatchOperation filterByCriterias = Aggregation.match(criteriaSuburb.andOperator(criteriaAccomm, criteriaMinNights, criteriaPriceRange));
		ProjectionOperation projectFields = Aggregation.project("_id", "name", "accommodates", "price");
		SortOperation sortPriceDesc = Aggregation.sort(Sort.Direction.DESC, "price");

		Aggregation pipeline = Aggregation.newAggregation(filterByCriterias, projectFields, sortPriceDesc);

		List<Document> results = template.aggregate(pipeline, "listings", Document.class).getMappedResults();

		List<AccommodationSummary> accoms = results.stream()
											.map(doc -> {
												AccommodationSummary as = new AccommodationSummary();
												as.setId(doc.getString("_id"));
												as.setName(doc.getString("name"));
												as.setAccomodates(doc.getInteger("accommodates"));
												as.setPrice(doc.get("price", Number.class).floatValue());
												
												return as;
											}).collect(Collectors.toList());

		return accoms;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
