// Demo dataset: 10 workers with services, reviews, and portfolio photos.
// Photos are Unsplash URLs (no Storage upload needed).

export const WORKERS = [
  {
    id: "demo_w_ravi_kumar",
    displayName: "Ravi Kumar",
    phone: "+919876500001",
    bio: "Licensed electrician with 12 years experience. Specialist in fan, light, and home wiring repairs. Same-day service in Mysuru.",
    photoUrl: "https://images.unsplash.com/photo-1531951657915-aab7d1f4cdde?w=400&q=80",
    town: "Mysuru",
    locality: "Vijayanagar",
    categories: ["electrician", "appliance_repair"],
    availability: "available",
    averageRating: 4.7,
    ratingCount: 23,
    thumbsUpCount: 19,
    services: [
      { title: "Ceiling fan repair", description: "Same-day repair, includes balancing", priceType: "fixed", priceInr: 200 },
      { title: "Tube light installation", description: "LED replacement, free disposal", priceType: "fixed", priceInr: 150 },
      { title: "Home wiring inspection", description: "Full safety check + report", priceType: "starting_at", priceInr: 500 },
      { title: "Switch board repair", description: "Per board, parts extra", priceType: "starting_at", priceInr: 250 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1621905251918-48416bd8575a?w=600&q=80", caption: "New panel installation" },
      { url: "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80", caption: "LED makeover for a living room" },
      { url: "https://images.unsplash.com/photo-1581094271901-8022df4466f9?w=600&q=80", caption: "Workshop bench" }
    ],
    reviews: [
      { customer: "Suma G", stars: 5, thumbsUp: true, text: "Came on time and fixed the fan in 20 minutes. Very polite." },
      { customer: "Manjunath", stars: 5, thumbsUp: true, text: "Best electrician in our area. Affordable and honest." },
      { customer: "Priya R", stars: 4, thumbsUp: true, text: "Good work, slightly delayed but apologized." }
    ]
  },

  {
    id: "demo_w_lakshmi_devi",
    displayName: "Lakshmi Devi",
    phone: "+919876500002",
    bio: "Tailoring all kinds of blouses, kurtis, and salwar suits. 15 years experience. Quick turnaround.",
    photoUrl: "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&q=80",
    town: "Mysuru",
    locality: "Saraswathipuram",
    categories: ["tailor"],
    availability: "available",
    averageRating: 4.9,
    ratingCount: 47,
    thumbsUpCount: 44,
    services: [
      { title: "Designer blouse stitching", description: "Includes 1 fitting trial", priceType: "starting_at", priceInr: 600 },
      { title: "Salwar suit", description: "Fabric not included", priceType: "starting_at", priceInr: 800 },
      { title: "Saree fall and pico", description: "Same day", priceType: "fixed", priceInr: 80 },
      { title: "Kurti alteration", description: "Up to 3 alterations", priceType: "fixed", priceInr: 120 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600&q=80", caption: "Bridal blouse work" },
      { url: "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&q=80", caption: "Salwar set finished" }
    ],
    reviews: [
      { customer: "Geetha S", stars: 5, thumbsUp: true, text: "She made a beautiful blouse for my daughter's wedding. Highly recommend!" },
      { customer: "Bhavya", stars: 5, thumbsUp: true, text: "Always perfect fit. My go-to tailor." }
    ]
  },

  {
    id: "demo_w_suresh_babu",
    displayName: "Suresh Babu",
    phone: "+919876500003",
    bio: "Plumbing services - leak repair, tap installation, bathroom fittings. Available 7 days a week.",
    photoUrl: "https://images.unsplash.com/photo-1622050787-cdc69d99e5e6?w=400&q=80",
    town: "Bengaluru",
    locality: "Jayanagar",
    categories: ["plumber"],
    availability: "busy",
    averageRating: 4.4,
    ratingCount: 31,
    thumbsUpCount: 25,
    services: [
      { title: "Tap leak repair", description: "Per tap, washer included", priceType: "fixed", priceInr: 150 },
      { title: "Bathroom fitting", description: "Full set installation", priceType: "starting_at", priceInr: 1200 },
      { title: "Drain cleaning", description: "Mechanical, 30 min", priceType: "fixed", priceInr: 350 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=600&q=80", caption: "Modular bathroom done" }
    ],
    reviews: [
      { customer: "Naveen K", stars: 4, thumbsUp: true, text: "Solved a leak two other plumbers couldn't. Reasonable price." },
      { customer: "Roopa", stars: 5, thumbsUp: true, text: "Very professional and clean work." }
    ]
  },

  {
    id: "demo_w_anita_rao",
    displayName: "Anita Rao",
    phone: "+919876500004",
    bio: "Carpenter specializing in custom furniture and wardrobe restoration. Workshop in Mangalore.",
    photoUrl: "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400&q=80",
    town: "Mangalore",
    locality: "Bejai",
    categories: ["carpenter"],
    availability: "available",
    averageRating: 4.8,
    ratingCount: 18,
    thumbsUpCount: 16,
    services: [
      { title: "Custom wardrobe", description: "Per linear foot", priceType: "starting_at", priceInr: 1800 },
      { title: "Door repair", description: "Hinges, alignment", priceType: "fixed", priceInr: 400 },
      { title: "Furniture polish", description: "Per piece", priceType: "starting_at", priceInr: 700 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600&q=80", caption: "Solid teak wardrobe" },
      { url: "https://images.unsplash.com/photo-1581539250439-c96689b516dd?w=600&q=80", caption: "Restored writing desk" },
      { url: "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80", caption: "Custom shelving" }
    ],
    reviews: [
      { customer: "Vikram M", stars: 5, thumbsUp: true, text: "Beautiful craftsmanship. Took a week longer than promised but worth the wait." },
      { customer: "Sneha", stars: 5, thumbsUp: true, text: "Custom kitchen cabinets came out perfect." }
    ]
  },

  {
    id: "demo_w_mohan_lal",
    displayName: "Mohan Lal",
    phone: "+919876500005",
    bio: "Painter for interior and exterior walls. Free color consultation. 20 years experience.",
    photoUrl: "https://images.unsplash.com/photo-1521791136064-7986c2920216?w=400&q=80",
    town: "Hubballi",
    locality: "Vidyanagar",
    categories: ["painter"],
    availability: "available",
    averageRating: 4.5,
    ratingCount: 27,
    thumbsUpCount: 23,
    services: [
      { title: "Interior painting", description: "Per sq ft, paint included", priceType: "starting_at", priceInr: 18 },
      { title: "Exterior painting", description: "Weatherproof, per sq ft", priceType: "starting_at", priceInr: 22 },
      { title: "Texture finish", description: "Per sq ft", priceType: "starting_at", priceInr: 35 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1562259949-e8e7689d7828?w=600&q=80", caption: "Living room makeover" }
    ],
    reviews: [
      { customer: "Rashmi", stars: 5, thumbsUp: true, text: "His color sense is excellent. Loved the result." },
      { customer: "Arjun", stars: 4, thumbsUp: true, text: "Good quality but slightly expensive." }
    ]
  },

  {
    id: "demo_w_kavitha_n",
    displayName: "Kavitha N",
    phone: "+919876500006",
    bio: "Gardener and landscape designer. Specialist in low-maintenance and organic kitchen gardens.",
    photoUrl: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&q=80",
    town: "Bengaluru",
    locality: "Indiranagar",
    categories: ["gardener"],
    availability: "available",
    averageRating: 4.6,
    ratingCount: 12,
    thumbsUpCount: 11,
    services: [
      { title: "Garden setup", description: "Soil + plants + first month care", priceType: "starting_at", priceInr: 3500 },
      { title: "Monthly maintenance", description: "Weekly visits, all tools included", priceType: "fixed", priceInr: 2000 },
      { title: "Tree pruning", description: "Per tree, up to 15 ft", priceType: "fixed", priceInr: 800 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600&q=80", caption: "Balcony herb garden" },
      { url: "https://images.unsplash.com/photo-1466692476868-aef1dfb1e735?w=600&q=80", caption: "Backyard transformation" }
    ],
    reviews: [
      { customer: "Madhuri P", stars: 5, thumbsUp: true, text: "She turned my dead balcony into a herb paradise. Worth every rupee." }
    ]
  },

  {
    id: "demo_w_imran_pasha",
    displayName: "Imran Pasha",
    phone: "+919876500007",
    bio: "Bike and scooter mechanic. Pickup and drop available within 5km. Genuine parts only.",
    photoUrl: "https://images.unsplash.com/photo-1622220419555-7b5b1c8b15db?w=400&q=80",
    town: "Belagavi",
    locality: "Camp",
    categories: ["mechanic"],
    availability: "available",
    averageRating: 4.3,
    ratingCount: 41,
    thumbsUpCount: 33,
    services: [
      { title: "General service", description: "Oil change, brake check, wash", priceType: "fixed", priceInr: 600 },
      { title: "Brake pad replacement", description: "Per wheel, parts extra", priceType: "fixed", priceInr: 250 },
      { title: "Engine tuning", description: "Carb cleaning, plug check", priceType: "starting_at", priceInr: 800 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=600&q=80", caption: "Workshop view" }
    ],
    reviews: [
      { customer: "Rahul J", stars: 4, thumbsUp: true, text: "Honest mechanic. Doesn't recommend unnecessary work." },
      { customer: "Salma", stars: 5, thumbsUp: true, text: "Came home, picked up, dropped back. Very convenient." }
    ]
  },

  {
    id: "demo_w_basavaraj",
    displayName: "Basavaraj S",
    phone: "+919876500008",
    bio: "Mason for compound walls, plastering, and flooring. Team of 3 available for larger jobs.",
    photoUrl: "https://images.unsplash.com/photo-1607582544054-ddbed4d3a7e0?w=400&q=80",
    town: "Ballari",
    locality: "Gandhi Nagar",
    categories: ["mason"],
    availability: "busy",
    averageRating: 4.2,
    ratingCount: 9,
    thumbsUpCount: 7,
    services: [
      { title: "Compound wall", description: "Per running foot, materials extra", priceType: "starting_at", priceInr: 450 },
      { title: "Wall plastering", description: "Per sq ft", priceType: "starting_at", priceInr: 35 },
      { title: "Tile work", description: "Per sq ft, labour only", priceType: "starting_at", priceInr: 80 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=600&q=80", caption: "Compound wall finished" }
    ],
    reviews: [
      { customer: "Lakshmi", stars: 4, thumbsUp: true, text: "Solid work, on budget." }
    ]
  },

  {
    id: "demo_w_deepika_r",
    displayName: "Deepika R",
    phone: "+919876500009",
    bio: "AC and refrigerator repair. Authorized service for major brands. AMC plans available.",
    photoUrl: "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400&q=80",
    town: "Mysuru",
    locality: "Kuvempunagar",
    categories: ["appliance_repair", "electrician"],
    availability: "available",
    averageRating: 4.7,
    ratingCount: 36,
    thumbsUpCount: 32,
    services: [
      { title: "Split AC service", description: "Deep clean, gas check", priceType: "fixed", priceInr: 700 },
      { title: "Refrigerator repair", description: "Diagnosis included", priceType: "starting_at", priceInr: 400 },
      { title: "Annual maintenance contract", description: "Up to 4 visits/year", priceType: "starting_at", priceInr: 2500 }
    ],
    portfolio: [
      { url: "https://images.unsplash.com/photo-1556909114-44e3e9399a2c?w=600&q=80", caption: "AC service in progress" }
    ],
    reviews: [
      { customer: "Harish", stars: 5, thumbsUp: true, text: "Quick response, fair price. AMC was worth it." },
      { customer: "Pooja", stars: 4, thumbsUp: true, text: "Solved a 3-year-old fridge issue in one visit." }
    ]
  },

  {
    id: "demo_w_ramesh_naik",
    displayName: "Ramesh Naik",
    phone: "+919876500010",
    bio: "Personal driver for outstation trips and daily commute. 10+ years driving in Karnataka.",
    photoUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
    town: "Bengaluru",
    locality: "Banashankari",
    categories: ["driver"],
    availability: "available",
    averageRating: 4.6,
    ratingCount: 28,
    thumbsUpCount: 25,
    services: [
      { title: "Daily driver (8 hrs)", description: "Within city limits", priceType: "fixed", priceInr: 1200 },
      { title: "Outstation trip", description: "Per day, food + stay separate", priceType: "starting_at", priceInr: 1800 },
      { title: "Airport drop", description: "BLR airport, sedan", priceType: "fixed", priceInr: 1500 }
    ],
    portfolio: [],
    reviews: [
      { customer: "Sunil", stars: 5, thumbsUp: true, text: "Drove us to Coorg and back. Very safe driver." },
      { customer: "Deepa", stars: 4, thumbsUp: true, text: "Punctual and polite." }
    ]
  }
];
