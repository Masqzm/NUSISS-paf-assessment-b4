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