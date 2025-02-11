// #########
// Task 2.2
// #########
db.listings_and_reviews.findOne()

db.listings_and_reviews.aggregate([
    {
        $match: {
            'address.country': {
                $regex: 'Australia',
                $options: 'i'
            }
        }
    },
    {
        $out: 'listings'    // copy over aggregation to new collection
    }
])

//db.listings_and_reviews.drop()

db.listings.findOne()
db.listings.find({}).limit(2)

// #########
// Task 2.3
// #########
db.listings.aggregate([
    {
        $unwind: '$reviews'
    },
    {
        $project: { 
            _id: '$reviews._id',
            date: '$reviews.date',
            listing_id: '$reviews.listing_id',
            reviewer_name: { $replaceAll: { input: '$reviews.reviewer_name', find: ',', replacement: '' } },
            comments: { 
                $replaceAll: { 
                    input: { $replaceAll: { input: '$reviews.comments', find: '\n', replacement: '' } }, 
                    find: '\r', 
                    replacement: '' 
                }
            }
        }
    },
    {
        $out: 'reviews'    // copy over aggregation to new collection
    }
])

db.reviews.findOne()


// #########
// Task 2.4
// #########
db.listings.updateMany(
    {},    // filter (e.g. use { address.market: "xxx" } instead if you want to unset certain docs w address.market xxx)
    { 
        $unset: { reviews: 1 }    // can unset more than 1 field
    }
)

db.listings.findOne()


// #######
// Task 3
// #######
db.listings.distinct('address.suburb').length

db.listings.aggregate([
    // Filter out field where values are ne null and ne ""
    {
        $match: {
            'address.country': {
				$regex: 'Australia',
				$options: 'i'
			},
            'address.suburb': { $ne: null, $ne: "" }
        }
    },
    {
        $group: {
            _id: "$address.suburb"  // Group by 'address.suburb' and set it as _id
        }
    },
    {
        $sort:  { _id: 1 }
    }
    //{ $count: "uniqueSuburb" }    // to check unique suburb count
])


// #######
// Task 4
// #######
db.listings.find({}).sort({
    price: 1
})
db.listings.aggregate([
    {
        $match: {
            'address.suburb': { 
				$regex: 'Lilyfield',
				$options: 'i'
			},
			accommodates: { $gte: 2 },
			min_nights: { $lte: 2 },
			price: { $lte: 1000 }
			// max_nights: { $gte: 2 },
            // price: { $gte: 50, $lte: 100 }
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