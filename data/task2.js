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