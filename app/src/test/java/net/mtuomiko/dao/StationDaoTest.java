package net.mtuomiko.dao;

//@QuarkusTest
//public class StationDaoTest {
//
//    @Inject
//    private Datastore datastore;
//    @Inject
//    private StationDao stationDao;
//
//    void emptyDatastore() {
//        Query<Entity> query = Query.newEntityQueryBuilder().setKind(StationEntity.KIND).build();
//        var result = datastore.run(query);
//        result.forEachRemaining(entity -> datastore.delete(entity.getKey()));
//        datastore.delete(datastore.newKeyFactory().setKind(StationListEntity.KIND).newKey("stationList"));
//    }
//
//    void initializeDatastore() {
//
//    }
//
//    @Test
//    public void whenNoStationListPresent_getStations_throws() {
//        emptyDatastore();
//
//        assertThatThrownBy(() -> {
//            stationDao.getStations();
//
//        }).isInstanceOf(IllegalStateException.class);
//    }
//}
