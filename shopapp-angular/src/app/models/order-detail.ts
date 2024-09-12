type Product = {
  id: number;
  name: string;
  price: number;
  description: string;
  category: {
    id: number;
    name: string;
  };
  created_at: string;
  updated_at: string;
  thumbnail_url: string;
};

export interface OrderDetail {
  id: number;
  product: Product;
  price: number;
  number_of_products: number;
  total_money: number;
  color: string;
}

/*
  {
      "id": 3,
      "product": {
          "id": 2,
          "name": "Ipad Pro",
          "price": 800.0,
          "description": "Description for this Ipad...",
          "category": {
              "id": 1,
              "name": "Đồ điện tử"
          },
          "created_at": "2024-04-29T11:46:30",
          "updated_at": "2024-04-29T11:46:30",
          "thumbnail_url": "73f72bc0-957a-46a9-be16-e2b717080a9b_nature-01.jpg"
      },
      "price": 800.0,
      "number_of_products": 5,
      "total_money": 4000.0,
      "color": null
  }
*/
