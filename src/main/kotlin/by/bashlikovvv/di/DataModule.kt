package by.bashlikovvv.di

import by.bashlikovvv.data.local.dao.*
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val dataModule =
    module {
        single<Database> {
            Database.connect(
                url = "jdbc:postgresql://localhost:55000/food_delivery",
                user = "postgres",
                driver = "org.postgresql.Driver",
                password = "0123456",
            )
        }

        single<StatusesService> {
            val database: Database = get()

            StatusesService(database)
        }

        single<CountriesService> {
            val database: Database = get()

            CountriesService(database)
        }

        single<ProducerCountriesService> {
            val database: Database = get()

            ProducerCountriesService(database)
        }

        single<ProductGroupsService> {
            val database: Database = get()

            ProductGroupsService(database)
        }

        single<NutritionFactsService> {
            val database: Database = get()

            NutritionFactsService(database)
        }

        single<UserTypesService> {
            val database: Database = get()

            UserTypesService(database)
        }

        single<CitiesService> {
            val database: Database = get()

            CitiesService(database)
        }

        single<StreetsService> {
            val database: Database = get()

            StreetsService(database)
        }

        single<AddressesService> {
            val database: Database = get()

            AddressesService(database)
        }

        single<UserAddressService> {
            val database: Database = get()

            UserAddressService(database)
        }

        single<ProducersService> {
            val database: Database = get()

            ProducersService(database)
        }

        single<ProductsService> {
            val database: Database = get()

            ProductsService(database)
        }

        single<AmountsService> {
            val database: Database = get()

            AmountsService(database)
        }

        single<PaymentCartsService> {
            val database: Database = get()

            PaymentCartsService(database)
        }

        single<UsersService> {
            val database: Database = get()

            UsersService(database)
        }

        single<CartsService> {
            val database: Database = get()

            CartsService(database)
        }

        single<OrderStatusesService> {
            val database: Database = get()

            OrderStatusesService(database)
        }

        single<OrderRatesService> {
            val database: Database = get()

            OrderRatesService(database)
        }

        single<OrdersService> {
            val database: Database = get()

            OrdersService(database)
        }
    }
