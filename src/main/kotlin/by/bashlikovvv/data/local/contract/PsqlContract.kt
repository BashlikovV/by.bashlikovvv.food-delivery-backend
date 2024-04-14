package by.bashlikovvv.data.local.contract

object PsqlContract {
    const val DATABASE_NAME = "food_delivery"

    object StatusesTable {
        const val TABLE_NAME = "statuses"
        const val COLUMN_ID = "status_id"
        const val COLUMN_NAME = "status_name"
    }

    object CountriesTable {
        const val TABLE_NAME = "countries"
        const val COLUMN_ID = "countries_id"
        const val COLUMN_NAME = "countries_name"
    }

    object ProducerCountriesTable {
        const val TABLE_NAME = "producer_countries"
        const val COLUMN_ID = "producer_country_id"
        const val COLUMN_NAME = "producer_country_name"
    }

    object ProductGroupsTable {
        const val TABLE_NAME = "product_groups"
        const val COLUMN_ID = "product_group_id"
        const val COLUMN_NAME = "product_group_name"
    }

    object NutritionFactsTable {
        const val TABLE_NAME = "nutrition_facts"
        const val COLUMN_ID = "nutrition_fact_id"
        const val COLUMN_PROTEINS = "nutrition_fact_proteins"
        const val COLUMN_LIPIDS = "nutrition_fact_lipids"
        const val COLUMN_GLUCIDES = "nutrition_fact_glucides"
        const val COLUMN_CALORIES = "nutrition_fact_calories"
    }

    object UserTypesTable {
        const val TABLE_NAME = "user_types"
        const val COLUMN_ID = "user_type_id"
        const val COLUMN_NAME = "user_type_name"
    }

    object CitiesTable {
        const val TABLE_NAME = "cities"
        const val COLUMN_ID = "cities_id"
        const val COLUMN_NAME = "cities_name"
        const val COLUMN_TIME_ZONE = "cities_timezone"
        const val COLUMN_CITIES_COUNTRIES_FK = "countries_fk"
    }

    object StreetsTable {
        const val TABLE_NAME = "streets"
        const val COLUMN_ID = "streets_id"
        const val COLUMN_NAME = "streets_name"
    }

    object AddressesTable {
        const val TABLE_NAME = "addresses"
        const val COLUMN_ID = "addresses_id"
        const val COLUMN_HOUSE = "addresses_house"
        const val COLUMN_FLOOR = "addresses_floor"
        const val COLUMN_APARTMENT = "addresses_appartment"
        const val COLUMN_POSTCODE = "addesses_postcode"
        const val COLUMN_ADDRESS_CITIES_FK = "addresses_cities_fk"
        const val COLUMN_ADDRESS_STREETS_FK = "addresses_streets_fk"
    }

    object UserAddressTable {
        const val TABLE_NAME = "user_address"
        const val COLUMN_ID = "user_address_id"
        const val COLUMN_USER_ADDRESS_ADDRESS_FK = "user_address_address_fk"
    }

    object ProducersTable {
        const val TABLE_NAME = "producers"
        const val COLUMN_ID = "producer_id"
        const val COLUMN_NAME = "producer_name"
        const val COLUMN_PRODUCER_COUNTRY_FK = "producer_country_fk"
    }

    object ProductsTable {
        const val TABLE_NAME = "products"
        const val COLUMN_ID = "product_id"
        const val COLUMN_DESCRIPTION = "product_description"
        const val COLUMN_NAME = "product_name"
        const val COLUMN_PRODUCT_GROUP_FK = "product_group_fk"
        const val COLUMN_PRODUCT_PRODUCER_FK = "product_producer_fk"
        const val COLUMN_PRODUCT_NUTRITION_FACT_FK = "product_nutrition_fact_fk"
    }

    object AmountsTable {
        const val TABLE_NAME = "amounts"
        const val COLUMN_ID = "amount_id"
        const val COLUMN_DELIVERY = "amount_delivery"
        const val COLUMN_DISCOUNT = "amount_dicount"
        const val COLUMN_AMOUNT = "amount_amount"
    }

    object PaymentCartsTable {
        const val TABLE_NAME = "user_payment_carts"
        const val COLUMN_ID = "user_payment_cart_id"
        const val COLUMN_NUMBER = "user_payment_cart_number"
        const val COLUMN_SYSTEM = "user_payment_cart_system"
        const val COLUMN_DEFAULT = "user_payment_cart_default"
        const val COLUMN_EMAIL = "user_payment_cart_email"
    }

    object UsersTable {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "user_id"
        const val COLUMN_EMAIL = "user_email"
        const val COLUMN_SALT = "user_salt"
        const val COLUM_HASH = "user_hash"
        const val COLUMN_PHONE = "user_phone"
        const val COLUMN_FIRSTNAME = "user_firstname"
        const val COLUMN_LASTNAME = "user_lastname"
        const val COLUMN_USER_TYPE_FK = "user_type_fk"
        const val COLUMN_BIRTHDATE = "user_birthdate"
        const val COLUMN_USER_USER_ADDRESS_FK = "user_address_fk"
        const val COLUMN_USER_PAYMENT_CART_FK = "user_payment_cart_fk"
    }

    object CartsTable {
        const val TABLE_NAME = "carts"
        const val COLUMN_ID = "cart_id"
        const val COLUMN_SIZE = "cart_size"
        const val COLUMN_ITEMS = "cart_items"
        const val COLUMN_CART_AMOUNT_FK = "cart_amount_fk"
        const val COLUMN_CART_PRODUCT_FK = "cart_product_fk"
        const val COLUMN_CART_USER_FK = "cart_user_fk"
    }

    object OrderStatuses {
        const val TABLE_NAME = "order_statuses"
        const val COLUMN_ID = "order_status_id"
        const val COLUMN_LAST_UPDATE = "order_status_last_update"
        const val COLUMN_ORDER_STATUS_STATUS_FK = "order_status_status_fk"
    }

    object OrderRatesTable {
        const val TABLE_NAME = "order_rates"
        const val COLUMN_ID = "order_rate_id"
        const val COLUMN_STATUS = "order_rate_status"
        const val COLUMN_COMMENT = "order_rate_comment"
    }

    object OrdersTable {
        const val TABLE_NAME = ""
        const val COLUMN_ID = ""
        const val COLUMN_ORDER_USER_FK = ""
        const val COLUMN_ORDER_ADDRESS_FK = ""
        const val COLUMN_DATE = ""
        const val COLUMN_ORDER_RATE_FK = ""
        const val COLUM_ORDER_STATUS_FK = ""
    }
}
